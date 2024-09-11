package com.devicetracker.ui.dashbord.assets

import com.devicetracker.core.Constants
import com.google.firebase.Timestamp

data class Asset(
    var assetId: String? = null,
    val assetName: String = Constants.EMPTY_STR,
    val assetType: String? = null,
    val modelName: String? = null,
    val serialNumber: String? = null,
    val description: String = Constants.EMPTY_STR,
    val assetOwnerId: String? = null,
    val assetOwnerName: String? = null,
    val imageUrl: String = Constants.EMPTY_STR,
    val createdAt : Timestamp? = null,
    val updatedAt : Timestamp? = null
)
