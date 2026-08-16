package com.example.data.model

import java.util.UUID

/**
 * Data class representing a medication item stored in the medical warehouse.
 *
 * @property id Unique identifier for the medication item.
 * @property name Commercial or trade name of the medication.
 * @property chemicalComposition Active chemical substances and formulation.
 * @property manufacturer Pharmaceutical company or laboratory producing the item.
 * @property currentStockLevel Available units in warehouse inventory.
 * @property expirationDate Expiry date of the batch (format: YYYY-MM-DD or MM/YYYY).
 * @property batchNumber Production batch/lot identification number.
 * @property unitPrice Net or unit purchase price.
 * @property minStockThreshold Minimum threshold before triggering a restock alert.
 */
data class MedicationItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val chemicalComposition: String,
    val manufacturer: String,
    val currentStockLevel: Int = 0,
    val expirationDate: String,
    val batchNumber: String = "",
    val unitPrice: Double = 0.0,
    val minStockThreshold: Int = 10
) {
    /**
     * Checks if current inventory is below or equal to the minimum threshold.
     */
    val isLowStock: Boolean
        get() = currentStockLevel <= minStockThreshold

    /**
     * Checks if item is completely out of stock.
     */
    val isOutOfStock: Boolean
        get() = currentStockLevel <= 0
}
