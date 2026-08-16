package com.example.data.repository

import com.example.data.local.dao.OrderDao
import com.example.data.local.entity.OrderEntity
import com.example.data.model.CartItem
import kotlinx.coroutines.flow.Flow
import org.json.JSONArray
import org.json.JSONObject

class OrderRepository(private val orderDao: OrderDao) {

    val allOrders: Flow<List<OrderEntity>> = orderDao.getAllOrders()

    fun getOrderById(id: Long): Flow<OrderEntity?> = orderDao.getOrderById(id)

    suspend fun saveOrder(
        cartItems: List<CartItem>,
        pharmacyName: String = "صيدلية الفواز",
        notes: String = ""
    ): Long {
        if (cartItems.isEmpty()) return -1L

        val totalSYP = cartItems
            .filter { it.agencyId != "celia" && it.agencyId != "masoud_serum" }
            .sumOf { it.netPrice * it.quantity }

        val totalUSD = cartItems
            .filter { it.agencyId == "celia" || it.agencyId == "masoud_serum" }
            .sumOf { it.netPrice * it.quantity }

        val itemsCount = cartItems.sumOf { it.quantity }
        val orderNum = "FWZ-${System.currentTimeMillis().toString().takeLast(6)}"

        val jsonArray = JSONArray()
        cartItems.forEach { item ->
            val obj = JSONObject().apply {
                put("drugId", item.drugId)
                put("tradeName", item.tradeName)
                put("agencyName", item.agencyName)
                put("agencyId", item.agencyId)
                put("netPrice", item.netPrice)
                put("bonusRatio", item.bonusRatio)
                put("netCode", item.netCode)
                put("quantity", item.quantity)
            }
            jsonArray.put(obj)
        }

        val orderEntity = OrderEntity(
            orderNumber = orderNum,
            timestamp = System.currentTimeMillis(),
            pharmacyName = pharmacyName.ifBlank { "صيدلية المستقبل" },
            totalSYP = totalSYP,
            totalUSD = totalUSD,
            itemsCount = itemsCount,
            status = "قيد المعالجة",
            notes = notes,
            itemsJson = jsonArray.toString()
        )

        return orderDao.insertOrder(orderEntity)
    }

    suspend fun updateOrderStatus(id: Long, status: String) {
        orderDao.updateOrderStatus(id, status)
    }

    suspend fun deleteOrder(id: Long) {
        orderDao.deleteOrderById(id)
    }

    suspend fun clearAllOrders() {
        orderDao.deleteAllOrders()
    }

    companion object {
        fun parseItemsJson(jsonStr: String): List<CartItem> {
            val list = mutableListOf<CartItem>()
            try {
                val array = JSONArray(jsonStr)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    list.add(
                        CartItem(
                            drugId = obj.optString("drugId", ""),
                            tradeName = obj.optString("tradeName", ""),
                            agencyName = obj.optString("agencyName", ""),
                            agencyId = obj.optString("agencyId", ""),
                            netPrice = obj.optDouble("netPrice", 0.0),
                            bonusRatio = obj.optString("bonusRatio", ""),
                            netCode = obj.optString("netCode", ""),
                            quantity = obj.optInt("quantity", 1)
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list
        }
    }
}
