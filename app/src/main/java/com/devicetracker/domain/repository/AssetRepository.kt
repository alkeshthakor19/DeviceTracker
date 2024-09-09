package com.devicetracker.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetHistory
import com.devicetracker.ui.dashbord.member.Member


typealias AddAssetResponse = Response<Boolean>
typealias GetAssetsResponse = List<Asset>
typealias GetAssetsByIdResponse = Asset?
typealias GetAssignHistoriesResponse = List<AssetHistory>
typealias UpdateAssetResponse = Response<Boolean>

interface AssetRepository {
    suspend fun addAsset(assetName: String, assetType: String, model: String, description: String ,selectedMember: Member, imageUrl: String): AddAssetResponse

    suspend fun uploadImageAndAddNewAssetToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        model: String,
        description: String,
        selectedMember: Member,
        onNavUp: () -> Unit
    ) : AddAssetResponse

    suspend fun getAssetsFromFirebase() : GetAssetsResponse

    suspend fun getAssetsDetailById(assetId: String) : GetAssetsByIdResponse

    suspend fun getPreviousAssignHistoriesByAssetId(assetId: String) : GetAssignHistoriesResponse

    suspend fun updateAsset(
        assetId: String,
        assetName: String,
        assetType: String,
        assetModelName: String,
        description: String,
        selectedOwner: Member?,
        imageUrl: String?
    ): UpdateAssetResponse

    suspend fun uploadImageAndUpdateAsset(
        assetId: String,
        isNeedToUpdateImageUrl: Boolean,
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        assetModelName: String,
        description: String,
        selectedOwner: Member?,
        onNavUp: () -> Unit
    ): UpdateAssetResponse
}