package com.devicetracker.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetHistory
import com.devicetracker.ui.dashbord.member.Member


typealias AddAssetResponse = Response<Boolean>
typealias GetAssetsResponse = List<Asset>
typealias GetAssetsByIdResponse = Asset
typealias GetAssignHistoriesResponse = List<AssetHistory>
typealias UpdateAssetResponse = Response<Boolean>
typealias AddModelResponse = Response<Boolean>

interface AssetRepository {
    suspend fun addAsset(assetName: String, assetType: String, modelName: String, serialNumber: String, description: String ,selectedMember: Member, imageUrl: String, assetId: String, assetQuantity: String, projectName: String, assetWorkingStatus: Boolean): AddAssetResponse

    suspend fun uploadImageAndAddNewAssetToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        modelName: String,
        serialNumber: String,
        description: String,
        selectedMember: Member,
        assetId: String,
        assetQuantity: String,
        projectName: String,
        assetWorkingStatus: Boolean,
        onNavUp: () -> Unit
    ) : AddAssetResponse

    suspend fun getAssetsFromFirebase() : GetAssetsResponse

    suspend fun getAssetsDetailById(assetDocId: String) : GetAssetsByIdResponse

    suspend fun getPreviousAssignHistoriesByAssetId(assetDocId: String) : GetAssignHistoriesResponse

    suspend fun updateAsset(
        assetDocId: String,
        assetName: String,
        assetType: String,
        modelName: String,
        serialNumber: String,
        description: String,
        selectedOwner: Member?,
        imageUrl: String?,
        assetId: String,
        assetQuantity: String,
        projectName: String,
        assetWorkingStatus: Boolean
    ): UpdateAssetResponse

    suspend fun uploadImageAndUpdateAsset(
        assetDocId: String,
        isNeedToUpdateImageUrl: Boolean,
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        modelName: String,
        serialNumber: String,
        description: String,
        selectedOwner: Member?,
        assetId: String,
        assetQuantity: String,
        projectName: String,
        assetWorkingStatus: Boolean,
        onNavUp: () -> Unit
    ): UpdateAssetResponse

    suspend fun getAssetListByMemberId(memberId: String) : List<Asset>

    suspend fun addModel(assetType: String, model: String): AddModelResponse

    suspend fun getModelsForAssetType(assetType: String): List<String>

    suspend fun getAssetsByAssetType(assetType: String) : List<Asset>

    suspend fun isAssetEditablePermission() : Boolean

    suspend fun deleteAsset(assetDocId: String, onSuccess: () -> Unit)
}