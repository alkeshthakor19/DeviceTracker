package com.devicetracker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.devicetracker.core.Constants.ADMIN_EMAIL
import com.devicetracker.core.Constants.ASSET_DESCRIPTION
import com.devicetracker.core.Constants.ASSET_ID
import com.devicetracker.core.Constants.IMAGE_URL
import com.devicetracker.core.Constants.ASSET_MODEL_NAME
import com.devicetracker.core.Constants.ASSET_NAME
import com.devicetracker.core.Constants.ASSET_OWNER_ID
import com.devicetracker.core.Constants.ASSET_OWNER_NAME
import com.devicetracker.core.Constants.ASSET_SERIAL_NUMBER
import com.devicetracker.core.Constants.ASSET_TYPE
import com.devicetracker.core.Constants.COLLECTION_ASSETS
import com.devicetracker.core.Constants.COLLECTION_ASSETS_HISTORY
import com.devicetracker.core.Constants.COLLECTION_ASSETS_MODELS
import com.devicetracker.core.Constants.CREATED_AT
import com.devicetracker.core.Constants.EMPTY_STR
import com.devicetracker.core.Constants.FIRE_STORAGE_IMAGES
import com.devicetracker.core.Constants.UNASSIGN_ID
import com.devicetracker.core.Constants.UPDATED_AT
import com.devicetracker.domain.models.Response.Failure
import com.devicetracker.domain.models.Response.Success
import com.devicetracker.domain.repository.AddAssetResponse
import com.devicetracker.domain.repository.AddModelResponse
import com.devicetracker.domain.repository.AssetRepository
import com.devicetracker.domain.repository.GetAssetsByIdResponse
import com.devicetracker.domain.repository.GetAssetsResponse
import com.devicetracker.domain.repository.GetAssignHistoriesResponse
import com.devicetracker.domain.repository.UpdateAssetResponse
import com.devicetracker.ui.dashbord.assets.Asset
import com.devicetracker.ui.dashbord.assets.AssetHistory
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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
        modelName: String,
        serialNumber: String,
        description: String,
        selectedMember: Member,
        imageUrl: String,
    ): AddAssetResponse = try {
        val assetOwnerName = if(selectedMember.memberId == UNASSIGN_ID) {
            EMPTY_STR
        } else {
            selectedMember.memberName
        }
        val asset = hashMapOf(
            ASSET_NAME to assetName,
            ASSET_TYPE to assetType,
            ASSET_MODEL_NAME to modelName,
            ASSET_SERIAL_NUMBER to serialNumber,
            ASSET_DESCRIPTION to description,
            ASSET_OWNER_ID to selectedMember.memberId,
            ASSET_OWNER_NAME to assetOwnerName,
            IMAGE_URL to imageUrl,
            CREATED_AT to serverTimestamp(),
            UPDATED_AT to serverTimestamp()
        )
        val result = db.collection(COLLECTION_ASSETS).add(asset).await()
        if(assetOwnerName.isNotEmpty()) {
            val assetHistory = hashMapOf(
                ASSET_ID to result.id,
                ASSET_OWNER_ID to selectedMember.memberId,
                ASSET_OWNER_NAME to selectedMember.memberName,
                ADMIN_EMAIL to FirebaseAuth.getInstance().currentUser?.email,
                CREATED_AT to serverTimestamp()
            )
            db.collection(COLLECTION_ASSETS_HISTORY).add(assetHistory).await()
        }
        Success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }

    override suspend fun addModel(
        assetType: String,
        model: String
    ): AddModelResponse = try {
        val asset = hashMapOf(
            ASSET_TYPE to assetType,
            ASSET_MODEL_NAME to model,
            CREATED_AT to serverTimestamp()
        )
        db.collection(COLLECTION_ASSETS_MODELS).add(asset).await()
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
        modelName: String,
        serialNumber: String,
        description: String,
        selectedMember: Member,
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
        addAsset(assetName, assetType,modelName, serialNumber, description, selectedMember, resultUri.toString())
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
    override suspend fun getAssetsDetailById(assetId: String): GetAssetsByIdResponse {
        val document = db.collection(COLLECTION_ASSETS).document(assetId).get().await()
        var asset : Asset? = null
        try {
            asset = document.toObject(Asset::class.java)
            if (asset != null) {
                asset.assetId = document.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("AssetRepositoryImpl", "getAssetsDetailById()")
        return asset
    }

    override suspend fun getPreviousAssignHistoriesByAssetId(assetId: String): GetAssignHistoriesResponse {
        val collectionRef = db.collection(COLLECTION_ASSETS_HISTORY)
        val query = collectionRef.whereEqualTo(ASSET_ID, assetId)
        val querySnapshot = query.orderBy(CREATED_AT, Query.Direction.DESCENDING).get().await()
        val assetHistories = mutableListOf<AssetHistory>()
        try {
            for (document in querySnapshot.documents) {
                val assetHistory = document.toObject(AssetHistory::class.java)
                if (assetHistory != null) {
                    assetHistory.id = document.id
                    assetHistories.add(assetHistory)
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return assetHistories
    }
    override suspend fun updateAsset(
        assetId: String,
        assetName: String,
        assetType: String,
        modelName: String,
        serialNumber: String,
        description: String,
        selectedOwner: Member?,
        imageUrl: String?
    ): UpdateAssetResponse {
        val assetOwnerName = if(selectedOwner != null && selectedOwner.memberId != UNASSIGN_ID) {
            selectedOwner.memberName
        } else {
            EMPTY_STR
        }
        val assetOwnerId = selectedOwner?.memberId ?: EMPTY_STR
        return try {
            val assetData = hashMapOf(
                ASSET_NAME to assetName,
                ASSET_TYPE to assetType,
                ASSET_MODEL_NAME to modelName,
                ASSET_SERIAL_NUMBER to serialNumber,
                ASSET_DESCRIPTION to description,
                ASSET_OWNER_ID to assetOwnerId,
                ASSET_OWNER_NAME to assetOwnerName,
                UPDATED_AT to serverTimestamp()
            )
            if (imageUrl != null) {
                assetData[IMAGE_URL] = imageUrl
            }
            val result = db.collection(COLLECTION_ASSETS).document(assetId).update(assetData).await()
            Log.d("AssetRepo", "nkp update result $result")
            if(assetOwnerName.isNotEmpty()) {
                val assetHistory = hashMapOf(
                    ASSET_ID to assetId,
                    ASSET_OWNER_ID to assetOwnerId,
                    ASSET_OWNER_NAME to assetOwnerName,
                    ADMIN_EMAIL to FirebaseAuth.getInstance().currentUser?.email,
                    CREATED_AT to serverTimestamp()
                )
                db.collection(COLLECTION_ASSETS_HISTORY).add(assetHistory).await()
            }
            Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Failure(e)
        }
    }
    override suspend fun uploadImageAndUpdateAsset(
        assetId: String,
        isNeedToUpdateImageUrl: Boolean,
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        modelName: String,
        serialNumber: String,
        description: String,
        selectedOwner: Member?,
        onNavUp: () -> Unit
    ): UpdateAssetResponse {
        return try {
            val resultUri = if(isNeedToUpdateImageUrl){
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
                resultUri.toString()
            } else {
                null
            }

            // After uploading the image, update the asset
            val updateResponse = updateAsset(
                assetId,
                assetName,
                assetType,
                modelName,
                serialNumber,
                description,
                selectedOwner,
                resultUri
            )
            if (updateResponse is Success && updateResponse.data) {
                onNavUp()
            }
            updateResponse
        } catch (e: Exception) {
            e.printStackTrace()
            Failure(e)
        }
    }

    override suspend fun getAssetListByMemberId(memberId: String): List<Asset> {
        val collectionRef = db.collection(COLLECTION_ASSETS)
        val query = collectionRef.whereEqualTo(ASSET_OWNER_ID, memberId)
        val querySnapshot = query.orderBy(UPDATED_AT, Query.Direction.DESCENDING).get().await()
        val assetList = mutableListOf<Asset>()
        try {
            for (document in querySnapshot.documents) {
                val asset = document.toObject(Asset::class.java)
                if (asset != null) {
                    asset.assetId = document.id
                    assetList.add(asset)
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return assetList
    }

    override suspend fun getModelsForAssetType(assetType: String): List<String> = try {
        val querySnapshot = db.collection(COLLECTION_ASSETS_MODELS)
            .whereEqualTo(ASSET_TYPE, assetType)
            .get()
            .await()

        querySnapshot.documents.mapNotNull { document ->
            document.getString(ASSET_MODEL_NAME)
        }
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }

    override suspend fun getAssetsByAssetType(assetType: String): List<Asset> {
        val collectionRef = db.collection(COLLECTION_ASSETS)
        val query = collectionRef.whereEqualTo(ASSET_TYPE, assetType)
        val querySnapshot = query.get().await()
        val assetList = mutableListOf<Asset>()
        try {
            for (document in querySnapshot.documents) {
                val asset = document.toObject(Asset::class.java)
                if (asset != null) {
                    asset.assetId = document.id
                    assetList.add(asset)
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return assetList
    }

}