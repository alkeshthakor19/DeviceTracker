package com.devicetracker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.devicetracker.core.Constants.COLLECTION_ASSETS
import com.devicetracker.core.Constants.COLLECTION_MEMBERS
import com.devicetracker.core.Constants.FIRE_STORAGE_IMAGES
import com.devicetracker.domain.models.Response.Failure
import com.devicetracker.domain.models.Response.Success
import com.devicetracker.domain.repository.AddAssetResponse
import com.devicetracker.domain.repository.AddMemberResponse
import com.devicetracker.domain.repository.AssetRepository
import com.devicetracker.domain.repository.GetAssetsByIdResponse
import com.devicetracker.domain.repository.GetAssetsResponse
import com.devicetracker.domain.repository.GetMembersByIdResponse
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AssetRepositoryImpl @Inject constructor(private val db: FirebaseFirestore, private val storageReference: StorageReference) : AssetRepository {
    override suspend fun addAsset(
        assetName: String,
        assetType: String,
        model: String,
        imageUrl: String,
    ): AddAssetResponse = try {
        val asset = hashMapOf(
            "assetName" to assetName,
            "assetType" to assetType,
            "model" to model,
            "imageUrl" to imageUrl,
            "createdAt" to serverTimestamp()
        )
        db.collection(COLLECTION_ASSETS).add(asset).await()
        Success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }

    override suspend fun uploadImageAndAddNewAssetToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        model: String,
        onNavUp: () -> Unit
    ): AddAssetResponse = try {
        val imageRef = storageReference.child("$FIRE_STORAGE_IMAGES/${UUID.randomUUID()}.jpg")
        val uploadTask = if (imageUri != null) {
            imageRef.putFile(imageUri)
        } else {
            val data = imageBitmap?.let {
                ByteArrayOutputStream().apply {
                    it.compress(Bitmap.CompressFormat.JPEG, 100, this)
                }.toByteArray()
            }
            imageRef.putBytes(data ?: byteArrayOf())
        }

        uploadTask.await()
        val resultUri = imageRef.downloadUrl.await()
        addAsset(assetName, assetType,model, resultUri.toString())
        onNavUp()
        Success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }

    override suspend fun getAssetsFromFirebase() : GetAssetsResponse {
        val querySnapshot = db.collection(COLLECTION_ASSETS).get().await()
        var assets : List<Asset> = emptyList()
        try {
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                assets = querySnapshot.documents.map { document ->
                    val asset = document.toObject(Asset::class.java) ?: Asset()
                    asset.assetId = document.id
                    asset
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return assets
        }
    override suspend fun getAssetsDetailById(assetId: String): GetAssetsByIdResponse = try {
        val documentSnapshot = db.collection(COLLECTION_ASSETS).document(assetId).get().await()
        Success(documentSnapshot)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }
}