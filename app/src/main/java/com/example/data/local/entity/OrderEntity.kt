package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val timestamp: Long = System.currentTimeMillis(),
    val pharmacyName: String = "صيدلية المستقبل",
    val totalSYP: Double = 0.0,
    val totalUSD: Double = 0.0,
    val itemsCount: Int = 0,
    val status: String = "قيد المعالجة", // "قيد المعالجة", "تم التجهيز", "تم الشحن", "مكتملة", "ملغاة"
    val notes: String = "",
    val itemsJson: String = "[]" // JSON string representing List<CartItem>
)
