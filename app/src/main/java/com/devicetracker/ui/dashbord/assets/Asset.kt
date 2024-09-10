package com.devicetracker.ui.dashbord.assets

import com.google.firebase.Timestamp

data class Asset(
    var assetId: String? = null,
    val assetName: String = "",
    val assetType: String? = null,
    val modelName: String? = null,
    val serialNumber: String? = null,
    val description: String = "",
    val assetOwnerId: String? = null,
    val assetOwnerName: String? = null,
    val imageUrl: String = "",
    val createdAt : Timestamp? = null,
    val updatedAt : Timestamp? = null
)