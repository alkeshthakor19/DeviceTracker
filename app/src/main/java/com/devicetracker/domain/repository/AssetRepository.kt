package com.devicetracker.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot


typealias AddAssetResponse = Response<Boolean>
typealias GetAssetsResponse = List<Asset>
typealias GetAssetsByIdResponse = Response<DocumentSnapshot?>

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
}