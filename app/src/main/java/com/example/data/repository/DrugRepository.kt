package com.example.data.repository

import com.example.data.local.DefaultDrugs
import com.example.data.model.Drug
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class DrugRepository {

    private val _drugsList = MutableStateFlow<List<Drug>>(DefaultDrugs.INITIAL_DRUGS)
    val drugsList: StateFlow<List<Drug>> = _drugsList.asStateFlow()

    fun getDrugById(id: String): Drug? {
        return _drugsList.value.find { it.id == id }
    }

    /**
     * Process JSON update according to user requirements:
     * When a new JSON file is imported:
     * - Parse the new drugs list
     * - Any drug present in the previous catalog that is missing in the new JSON will have `isAvailable = false` ("غير متوفر") or be removed from active view.
     * - Add/Update new drugs.
     */
    fun updateDrugsFromJson(jsonString: String): Result<Int> {
        return try {
            val newDrugs = mutableListOf<Drug>()
            val trimmed = jsonString.trim()

            if (trimmed.startsWith("[")) {
                val jsonArray = JSONArray(trimmed)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    newDrugs.add(parseDrugJsonObject(obj))
                }
            } else if (trimmed.startsWith("{")) {
                val rootObj = JSONObject(trimmed)
                if (rootObj.has("الشركات")) {
                    val companiesObj = rootObj.getJSONObject("الشركات")
                    val companyKeys = companiesObj.keys()
                    while (companyKeys.hasNext()) {
                        val compName = companyKeys.next()
                        val compData = companiesObj.getJSONObject(compName)
                        if (compData.has("الأدوية")) {
                            val drugsArr = compData.getJSONArray("الأدوية")
                            val agencyIdKey = mapCompanyNameId(compName)
                            for (j in 0 until drugsArr.length()) {
                                val item = drugsArr.getJSONObject(j)
                                val num = item.optInt("رقم", j + 1)
                                val drugName = item.optString("اسم_الدواء", "دواء")
                                val netPriceVal = item.optDouble("السعر_الصافي", 0.0)
                                val bonusVal = item.optString("البونص", "")

                                newDrugs.add(
                                    Drug(
                                        id = "DRUG-${agencyIdKey}-${num}",
                                        tradeName = drugName,
                                        scientificName = "",
                                        agencyId = agencyIdKey,
                                        agencyName = compName,
                                        publicPrice = if (netPriceVal > 0) netPriceVal * 1.35 else 0.0,
                                        pharmacistPrice = if (netPriceVal > 0) netPriceVal * 1.15 else 0.0,
                                        netPrice = netPriceVal,
                                        netCode = "NET-${agencyIdKey}-${num}",
                                        bonusRatio = bonusVal,
                                        isAvailable = true
                                    )
                                )
                            }
                        }
                    }
                } else {
                    newDrugs.add(parseDrugJsonObject(rootObj))
                }
            }

            if (newDrugs.isEmpty()) {
                return Result.failure(IllegalArgumentException("ملف JSON لا يحتوي على بيانات أدوية صحيحة."))
            }

            val existingMap = _drugsList.value.associateBy { it.id }.toMutableMap()
            val newDrugIds = newDrugs.map { it.id }.toSet()

            val updatedList = mutableListOf<Drug>()
            updatedList.addAll(newDrugs.map { it.copy(isAvailable = true) })

            // Old drugs not present in new JSON update get marked as isAvailable = false
            existingMap.values.forEach { oldDrug ->
                if (oldDrug.id !in newDrugIds) {
                    updatedList.add(oldDrug.copy(isAvailable = false))
                }
            }

            _drugsList.value = updatedList
            Result.success(newDrugs.size)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun mapCompanyNameId(name: String): String {
        return when (name.trim()) {
            "دومنا" -> "domna"
            "بركات" -> "barakat"
            "ميديكو" -> "medico"
            "المتحدة" -> "allied"
            "ابن رشد" -> "ibn_rushd"
            "لاما", "لاما فارما" -> "lama"
            "هابي كيور" -> "happy_cure"
            "سيليا", "حليب سيليا" -> "celia"
            "مسعود", "سيروم مسعود" -> "masoud_serum"
            "ابن الهيثم" -> "ibn_al_haytham"
            "حياة فارما", "حياة فاما", "حيارة فارما" -> "hayat_pharma"
            "افاميا" -> "afamia"
            "يونيفارما" -> "unipharma"
            "الرازي" -> "al_razi"
            "الفا" -> "alpha"
            "فارما لاند" -> "pharma_land"
            "روي فيت" -> "roy_fit"
            "زين للمعقمات", "زين" -> "zein"
            "اميسا" -> "emessa"
            "كيمي" -> "chemi"
            "اوبري" -> "obari"
            "راشا" -> "rasha"
            "اسيا" -> "asia"
            "السورية" -> "syrian"
            "شفا" -> "shafa"
            "ابن حيان" -> "ibn_hayyan"
            "ميديوتيك" -> "mediotec"
            "اوغاريت" -> "sugarit"
            "ماجيكو" -> "magico"
            "راما فارما" -> "rama_pharma"
            "السعد" -> "al_saad"
            "الفارس" -> "al_fares"
            "بحري" -> "bahri"
            "بيوميد", "بيوميد فارما" -> "biomed"
            "ابن زهر" -> "ibn_zuhr"
            else -> name.trim().lowercase().replace(" ", "_")
        }
    }

    private fun parseDrugJsonObject(obj: JSONObject): Drug {
        val id = obj.optString("id", obj.optString("drugId", "DRUG-${System.currentTimeMillis()}"))
        val tradeName = obj.optString("tradeName", obj.optString("name", "دواء بدون اسم"))
        val scientificName = obj.optString("scientificName", obj.optString("genericName", ""))
        val agencyId = obj.optString("agencyId", "domna")
        val agencyName = obj.optString("agencyName", "مستودع الفواز")
        val publicPrice = obj.optDouble("publicPrice", obj.optDouble("price", 0.0))
        val pharmacistPrice = obj.optDouble("pharmacistPrice", publicPrice * 0.8)
        val netPrice = obj.optDouble("netPrice", pharmacistPrice * 0.85)
        val netCode = obj.optString("netCode", "NET-${id}")
        val bonusRatio = obj.optString("bonusRatio", obj.optString("bonus", ""))
        val isAvailable = obj.optBoolean("isAvailable", true)
        val description = obj.optString("description", "")
        val composition = obj.optString("composition", "")
        val dosage = obj.optString("dosage", "")
        val companyCode = obj.optString("companyCode", "")

        return Drug(
            id = id,
            tradeName = tradeName,
            scientificName = scientificName,
            agencyId = agencyId,
            agencyName = agencyName,
            publicPrice = publicPrice,
            pharmacistPrice = pharmacistPrice,
            netPrice = netPrice,
            netCode = netCode,
            bonusRatio = bonusRatio,
            isAvailable = isAvailable,
            description = description,
            composition = composition,
            dosage = dosage,
            companyCode = companyCode
        )
    }
}
