package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.DefaultDrugs
import com.example.data.local.entity.OrderEntity
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.DEFAULT_OFFERS
import com.example.data.model.Drug
import com.example.data.model.SortOption
import com.example.data.model.WarehouseOffer
import com.example.data.remote.GeminiService
import com.example.data.repository.DrugRepository
import com.example.data.repository.OrderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class JsonImportResult {
    data class Success(val importedCount: Int) : JsonImportResult()
    data class Error(val message: String) : JsonImportResult()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DrugRepository()
    private val geminiService = GeminiService()
    private val appDatabase = AppDatabase.getInstance(application)
    private val orderRepository = OrderRepository(appDatabase.orderDao())
    private val prefs = application.getSharedPreferences("fawaz_app_prefs", Context.MODE_PRIVATE)

    private val _favoriteDrugIds = MutableStateFlow<Set<String>>(
        prefs.getStringSet("favorite_drugs", emptySet()) ?: emptySet()
    )
    val favoriteDrugIds: StateFlow<Set<String>> = _favoriteDrugIds.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(
        if (prefs.contains("dark_mode_override")) prefs.getBoolean("dark_mode_override", false) else null
    )
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _inStockOnly = MutableStateFlow(false)
    val inStockOnly: StateFlow<Boolean> = _inStockOnly.asStateFlow()

    private val _sortBy = MutableStateFlow(SortOption.DEFAULT)
    val sortBy: StateFlow<SortOption> = _sortBy.asStateFlow()

    private val _offers = MutableStateFlow<List<WarehouseOffer>>(DEFAULT_OFFERS)
    val offers: StateFlow<List<WarehouseOffer>> = _offers.asStateFlow()

    init {
        loadBrochureFromAssets()
    }

    private fun loadBrochureFromAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>()
                val assetManager = context.assets
                val inputStream = assetManager.open("brochure_medications.json")
                val jsonString = inputStream.bufferedReader().use { it.readText() }
                repository.updateDrugsFromJson(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val orders: StateFlow<List<OrderEntity>> = orderRepository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedAgency = MutableStateFlow<String?>(null)
    val selectedAgency: StateFlow<String?> = _selectedAgency.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedDrug = MutableStateFlow<Drug?>(null)
    val selectedDrug: StateFlow<Drug?> = _selectedDrug.asStateFlow()

    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    val cartTotalNet: StateFlow<Double> = _cartItems.map { items ->
        items.sumOf { it.netPrice * it.quantity }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome",
                sender = ChatSender.AI,
                text = "مرحباً بك في مستودع الفواز للأدوية! أنا مساعدك الذكي الصيدلاني. كيف يمكنني مساعدتك اليوم في الاستفسار عن الأدوية، الوكالات الحصرية، أو تحليل الروشيتات؟"
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val _jsonImportState = MutableStateFlow<JsonImportResult?>(null)
    val jsonImportState: StateFlow<JsonImportResult?> = _jsonImportState.asStateFlow()

    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    val filteredDrugs: StateFlow<List<Drug>> = combine(
        repository.drugsList,
        _selectedAgency,
        _searchQuery,
        _inStockOnly,
        _sortBy
    ) { drugs, agency, query, inStock, sort ->
        val filtered = drugs.filter { drug ->
            val matchesAgency = agency == null || drug.agencyId.equals(agency, ignoreCase = true)
            val q = query.trim()
            val matchesQuery = q.isEmpty() ||
                    drug.tradeName.contains(q, ignoreCase = true) ||
                    drug.scientificName.contains(q, ignoreCase = true) ||
                    drug.netCode.contains(q, ignoreCase = true) ||
                    drug.companyCode.contains(q, ignoreCase = true) ||
                    drug.agencyName.contains(q, ignoreCase = true)
            val matchesStock = !inStock || drug.isAvailable

            matchesAgency && matchesQuery && matchesStock
        }

        when (sort) {
            SortOption.DEFAULT -> filtered
            SortOption.PRICE_ASC -> filtered.sortedBy { it.netPrice }
            SortOption.PRICE_DESC -> filtered.sortedByDescending { it.netPrice }
            SortOption.BONUS_DESC -> filtered.sortedByDescending { it.bonusRatio }
            SortOption.NAME_ASC -> filtered.sortedBy { it.tradeName }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteDrugs: StateFlow<List<Drug>> = combine(
        repository.drugsList,
        _favoriteDrugIds
    ) { drugs, favIds ->
        drugs.filter { it.id in favIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(drugId: String) {
        val current = _favoriteDrugIds.value.toMutableSet()
        val isFav = if (current.contains(drugId)) {
            current.remove(drugId)
            false
        } else {
            current.add(drugId)
            true
        }
        _favoriteDrugIds.value = current
        prefs.edit().putStringSet("favorite_drugs", current).apply()

        viewModelScope.launch {
            if (isFav) {
                _toastEvent.emit("❤️ تمت إضافة الدواء إلى المفضلة")
            } else {
                _toastEvent.emit("تمت إزالة الدواء من المفضلة")
            }
        }
    }

    fun isFavorite(drugId: String): Boolean {
        return _favoriteDrugIds.value.contains(drugId)
    }

    fun toggleDarkMode(systemIsDark: Boolean) {
        val current = _isDarkMode.value ?: systemIsDark
        val next = !current
        _isDarkMode.value = next
        prefs.edit().putBoolean("dark_mode_override", next).apply()
    }

    fun setInStockOnly(value: Boolean) {
        _inStockOnly.value = value
    }

    fun setSortBy(sort: SortOption) {
        _sortBy.value = sort
    }

    fun selectAgency(agencyId: String?) {
        _selectedAgency.value = agencyId
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openDrugDetails(drug: Drug) {
        _selectedDrug.value = drug
    }

    fun getDrugById(id: String): Drug? {
        return repository.getDrugById(id) ?: DefaultDrugs.INITIAL_DRUGS.find { it.id == id }
    }

    fun closeDrugDetails() {
        _selectedDrug.value = null
    }

    fun addToCart(drug: Drug, context: Context, quantity: Int = 1) {
        // Requirement check: If drug is marked NOT AVAILABLE ("غير متوفر"), prevent purchase and play error sound!
        if (!drug.isAvailable) {
            playErrorAudioAndVibration(context)
            viewModelScope.launch {
                _toastEvent.emit("⚠️ عذراً! هذا الدواء غير متوفر حالياً في مستودع الفواز ولا يمكن إضافته للطلب.")
            }
            return
        }

        val currentList = _cartItems.value.toMutableList()
        val existingIndex = currentList.indexOfFirst { it.drugId == drug.id }

        if (existingIndex >= 0) {
            val existing = currentList[existingIndex]
            currentList[existingIndex] = existing.copy(quantity = existing.quantity + quantity)
        } else {
            currentList.add(
                CartItem(
                    drugId = drug.id,
                    tradeName = drug.tradeName,
                    agencyName = drug.agencyName,
                    netPrice = drug.netPrice,
                    bonusRatio = drug.bonusRatio,
                    netCode = drug.netCode,
                    quantity = quantity
                )
            )
        }
        _cartItems.value = currentList
        viewModelScope.launch {
            _toastEvent.emit("تمت إضافة ${drug.tradeName} إلى سلة الطلب بنجاح")
        }
    }

    fun updateCartQuantity(drugId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeFromCart(drugId)
            return
        }
        _cartItems.value = _cartItems.value.map { item ->
            if (item.drugId == drugId) item.copy(quantity = newQuantity) else item
        }
    }

    fun removeFromCart(drugId: String) {
        _cartItems.value = _cartItems.value.filter { it.drugId != drugId }
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun sendAiMessage(prompt: String, image: android.graphics.Bitmap? = null) {
        if (prompt.isBlank() && image == null) return

        val userMessage = ChatMessage(
            id = System.currentTimeMillis().toString(),
            sender = ChatSender.USER,
            text = prompt,
            image = image
        )

        _chatMessages.value = _chatMessages.value + userMessage
        _isAiLoading.value = true

        viewModelScope.launch {
            val responseText = geminiService.generateDrugAiResponse(
                prompt = prompt,
                bitmap = image,
                catalogDrugs = repository.drugsList.value
            )

            val aiMessage = ChatMessage(
                id = (System.currentTimeMillis() + 1).toString(),
                sender = ChatSender.AI,
                text = responseText
            )

            _chatMessages.value = _chatMessages.value + aiMessage
            _isAiLoading.value = false
        }
    }

    fun importJson(jsonText: String) {
        val result = repository.updateDrugsFromJson(jsonText)
        if (result.isSuccess) {
            val count = result.getOrDefault(0)
            _jsonImportState.value = JsonImportResult.Success(count)
            viewModelScope.launch {
                _toastEvent.emit("✅ تم تحديث بيانات أدوية مستودع الفواز بنجاح! ($count دواء متوفر)")
            }
        } else {
            val errorMsg = result.exceptionOrNull()?.localizedMessage ?: "خطأ في تنسيق ملف JSON"
            _jsonImportState.value = JsonImportResult.Error(errorMsg)
        }
    }

    fun resetJsonImportState() {
        _jsonImportState.value = null
    }

    fun saveCurrentCartToOrderHistory(pharmacyName: String, notes: String) {
        val items = _cartItems.value
        if (items.isEmpty()) return
        viewModelScope.launch {
            val orderId = orderRepository.saveOrder(
                cartItems = items,
                pharmacyName = pharmacyName,
                notes = notes
            )
            if (orderId > 0) {
                _toastEvent.emit("📦 تم حفظ الطلبية في سجل الطلبيات بنجاح!")
            }
        }
    }

    fun updateOrderStatus(orderId: Long, status: String) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status)
            _toastEvent.emit("تم تحديث حالة الطلبية إلى: $status")
        }
    }

    fun deleteOrder(orderId: Long) {
        viewModelScope.launch {
            orderRepository.deleteOrder(orderId)
            _toastEvent.emit("تم حذف الطلبية من السجل")
        }
    }

    fun clearAllOrders() {
        viewModelScope.launch {
            orderRepository.clearAllOrders()
            _toastEvent.emit("تم مسح سجل الطلبيات بالكامل")
        }
    }

    fun reorderFromHistory(order: OrderEntity, context: Context) {
        val items = OrderRepository.parseItemsJson(order.itemsJson)
        if (items.isEmpty()) return
        _cartItems.value = items
        viewModelScope.launch {
            _toastEvent.emit("🛒 تم تحميل ${items.size} أصناف من الطلبية إلى السلة الحالية!")
        }
    }

    private fun playErrorAudioAndVibration(context: Context) {
        viewModelScope.launch(Dispatchers.Default) {
            try {
                // Tone Generator Error Sound with automatic cleanup
                val toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 80)
                toneGenerator.startTone(ToneGenerator.TONE_SUP_ERROR, 300)
                delay(350)
                toneGenerator.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        try {
            // Device Vibration
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                val vibrator = vibratorManager.defaultVibrator
                vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                if (vibrator.hasVibrator()) {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(300)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
