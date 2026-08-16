package com.example.data.model

data class MedicineDetailInfo(
    val drug: Drug,
    val activeIngredients: String,
    val pharmaceuticalForm: String,
    val therapeuticClass: String,
    val adultDosage: String,
    val pediatricDosage: String,
    val usageInstructions: String,
    val indications: List<String>,
    val warningsAndPrecautions: List<String>,
    val contraindications: String,
    val storageConditions: String,
    val manufacturerName: String,
    val manufacturerOrigin: String,
    val representativeName: String,
    val representativePhone: String,
    val representativeArea: String,
    val estimatedProfitMargin: String
)
