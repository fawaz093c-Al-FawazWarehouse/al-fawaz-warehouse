package com.example.data.model

data class Drug(
    val id: String,
    val tradeName: String,
    val scientificName: String,
    val agencyId: String,
    val agencyName: String,
    val publicPrice: Double,
    val pharmacistPrice: Double,
    val netPrice: Double,
    val netCode: String,
    val bonusRatio: String = "",
    val isAvailable: Boolean = true,
    val description: String = "",
    val composition: String = "",
    val dosage: String = "",
    val companyCode: String = ""
) {
    val name: String get() = tradeName
    val company: String get() = agencyName
    val category: String get() = if (description.isNotBlank()) description else agencyName
    val bonus: String get() = bonusRatio
}
