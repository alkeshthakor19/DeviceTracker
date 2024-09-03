package com.devicetracker.ui.dashbord.assets

import com.google.firebase.Timestamp

data class AssetHistory(
    val id: String,
    val assetId: String,
    val assetOwnerId: String,
    val assetOwnerName: String,
    val adminEmail: String,
    val createdAt: Timestamp? = null
)