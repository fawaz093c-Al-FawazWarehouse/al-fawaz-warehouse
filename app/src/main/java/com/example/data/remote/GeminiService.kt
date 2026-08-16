package com.example.data.remote

import android.graphics.Bitmap
import com.example.BuildConfig
import com.example.data.model.Drug
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class GeminiService {

    private val client = OkHttpClient()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    suspend fun generateDrugAiResponse(
        prompt: String,
        bitmap: Bitmap?,
        catalogDrugs: List<Drug>
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            return@withContext "مرحباً بك! يرجى إضافة مفتاح Gemini API Key في إعدادات التطبيق للاستفادة الكاملة من المساعد الذكي لمستودع الفواز."
        }

        try {
            val catalogSummary = catalogDrugs.take(15).joinToString("\n") {
                "- ${it.tradeName} (${it.scientificName}) | وكالة: ${it.agencyName} | الصافي: $${it.netPrice} | العرض: ${it.bonusRatio} | الحالة: ${if (it.isAvailable) "متوفر" else "غير متوفر"}"
            }

            val systemInstruction = """
                أنت المساعد الذكي الصيدلاني المعتمد لمستودع الفواز للأدوية (Al-Fawaz Warehouse) - "حيث تبدأ الثقة".
                وظيفتك مساعدة الصيدلي والعميل في:
                1. تقديم الاستشارات الدوائية الدقيقة (الجرعة، التداخلات الدوائية، التركيب العلمي).
                2. معلومات وكالات الأدوية الحصرية الـ 8 (دومنا، بركات، ميديكو، المتحدة، ابن رشد، لاما، هابي كيور، حليب سيليا) وسيروم مسعود.
                3. التعرف على صور العبوات أو الروشيتات أو الجداول وتوضيح الأدوية المتاحة.
                4. الرد بلغة عربية صيدلانية واضحة، احترافية، ومحترمة.

                عينة من الأدوية المتاحة بالمستودع حالياً:
                $catalogSummary
            """.trimIndent()

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"

            val contentsArray = JSONArray()
            val userPartsArray = JSONArray()

            userPartsArray.put(JSONObject().put("text", "$systemInstruction\n\nسؤال المستخدم: $prompt"))

            if (bitmap != null) {
                val base64Image = bitmapToBase64(bitmap)
                val inlineDataObj = JSONObject().apply {
                    put("mime_type", "image/jpeg")
                    put("data", base64Image)
                }
                userPartsArray.put(JSONObject().put("inline_data", inlineDataObj))
            }

            contentsArray.put(JSONObject().put("parts", userPartsArray))

            val requestBodyJson = JSONObject().apply {
                put("contents", contentsArray)
            }

            val request = Request.Builder()
                .url(url)
                .post(requestBodyJson.toString().toRequestBody(jsonMediaType))
                .build()

            val response = client.newCall(request).execute()
            val responseBody = response.body?.string()

            if (response.isSuccessful && responseBody != null) {
                val jsonRes = JSONObject(responseBody)
                val candidates = jsonRes.optJSONArray("candidates")
                if (candidates != null && candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val contentObj = firstCandidate.optJSONObject("content")
                    val parts = contentObj?.optJSONArray("parts")
                    if (parts != null && parts.length() > 0) {
                        return@withContext parts.getJSONObject(0).optString("text", "لم أتمكن من معالجة الطلب حالياً.")
                    }
                }
            }
            return@withContext "عذراً، حدث خطأ أثناء الاتصال بالذكاء الاصطناعي لمستودع الفواز. يرجى المحاولة لاحقاً."
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext "مساعد الفواز الذكي: ${e.localizedMessage ?: "حدث خطأ غير متوقع"}"
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        val byteArray = outputStream.toByteArray()
        return android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
    }

    private fun getApiKey(): String {
        return try {
            val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}
