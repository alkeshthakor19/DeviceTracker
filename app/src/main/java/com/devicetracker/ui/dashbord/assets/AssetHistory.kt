package com.devicetracker.ui.dashbord.assets

import com.google.firebase.Timestamp

data class AssetHistory(
    var id: String? = null,
    val assetId: String? = null,
    val assetOwnerId: String? = null,
    val assetOwnerName: String? = null,
    val adminEmail: String? = null,
    val createdAt: Timestamp? = null
)