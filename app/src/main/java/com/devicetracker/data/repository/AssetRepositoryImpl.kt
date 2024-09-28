package com.devicetracker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.devicetracker.core.Constants.ADMIN_EMAIL
import com.devicetracker.core.Constants.ASSET_DESCRIPTION
import com.devicetracker.core.Constants.ASSET_DOC_ID
import com.devicetracker.core.Constants.ASSET_ID
import com.devicetracker.core.Constants.ASSET_MODEL_NAME
import com.devicetracker.core.Constants.ASSET_NAME
import com.devicetracker.core.Constants.ASSET_OWNER_ID
import com.devicetracker.core.Constants.ASSET_OWNER_NAME
import com.devicetracker.core.Constants.ASSET_QUANTITY
import com.devicetracker.core.Constants.ASSET_SERIAL_NUMBER
import com.devicetracker.core.Constants.ASSET_TYPE
import com.devicetracker.core.Constants.ASSET_WORKING_STATUS
import com.devicetracker.core.Constants.COLLECTION_ASSETS
import com.devicetracker.core.Constants.COLLECTION_ASSETS_HISTORY
import com.devicetracker.core.Constants.COLLECTION_ASSETS_MODELS
import com.devicetracker.core.Constants.COLLECTION_MEMBERS
import com.devicetracker.core.Constants.CREATED_AT
import com.devicetracker.core.Constants.EMAIL_ADDRESS
import com.devicetracker.core.Constants.EMPTY_STR
import com.devicetracker.core.Constants.FIRE_STORAGE_IMAGES
import com.devicetracker.core.Constants.IMAGE_URL
import com.devicetracker.core.Constants.LAST_VERIFICATION_AT
import com.devicetracker.core.Constants.PROJECT_NAME
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
        assetId: String,
        assetQuantity: String,
        projectName: String,
        assetWorkingStatus: Boolean
    ): AddAssetResponse = try {
        val assetOwnerName = if(selectedMember.memberId == UNASSIGN_ID) {
            EMPTY_STR
        } else {
            selectedMember.memberName
        }
        val asset = hashMapOf(
            ASSET_NAME to assetName,
            ASSET_ID to assetId,
            ASSET_TYPE to assetType,
            ASSET_MODEL_NAME to modelName,
            ASSET_SERIAL_NUMBER to serialNumber,
            ASSET_QUANTITY to assetQuantity,
            PROJECT_NAME to projectName,
            ASSET_DESCRIPTION to description,
            ASSET_OWNER_ID to selectedMember.memberId,
            ASSET_OWNER_NAME to assetOwnerName,
            IMAGE_URL to imageUrl,
            ASSET_WORKING_STATUS to assetWorkingStatus,
            LAST_VERIFICATION_AT to serverTimestamp(),
            CREATED_AT to serverTimestamp(),
            UPDATED_AT to serverTimestamp()
        )
        val result = db.collection(COLLECTION_ASSETS).add(asset).await()
        if(assetOwnerName.isNotEmpty()) {
            val assetHistory = hashMapOf(
                ASSET_DOC_ID to result.id,
                ASSET_ID to assetId,
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
        assetId: String,
        assetQuantity: String,
        projectName: String,
        assetWorkingStatus: Boolean,
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
        addAsset(assetName, assetType,modelName, serialNumber, description, selectedMember, resultUri.toString(), assetId, assetQuantity, projectName, assetWorkingStatus)
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
                    asset.assetDocId = document.id
                    asset
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return assets
    }
    override suspend fun getAssetsDetailById(assetDocId: String): GetAssetsByIdResponse {
        val document = db.collection(COLLECTION_ASSETS).document(assetDocId).get().await()
        var asset = Asset()
        try {
            if( document.toObject(Asset::class.java) != null) {
                asset = document.toObject(Asset::class.java)!!
                asset.assetDocId = document.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return asset
    }

    override suspend fun getPreviousAssignHistoriesByAssetId(assetDocId: String): GetAssignHistoriesResponse {
        val collectionRef = db.collection(COLLECTION_ASSETS_HISTORY)
        val query = collectionRef.whereEqualTo(ASSET_DOC_ID, assetDocId)
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
                ASSET_ID to assetId,
                ASSET_TYPE to assetType,
                ASSET_MODEL_NAME to modelName,
                ASSET_SERIAL_NUMBER to serialNumber,
                ASSET_QUANTITY to assetQuantity,
                PROJECT_NAME to projectName,
                ASSET_DESCRIPTION to description,
                ASSET_OWNER_ID to assetOwnerId,
                ASSET_OWNER_NAME to assetOwnerName,
                ASSET_WORKING_STATUS to assetWorkingStatus,
                UPDATED_AT to serverTimestamp()
            )
            if (imageUrl != null) {
                assetData[IMAGE_URL] = imageUrl
            }
            val result = db.collection(COLLECTION_ASSETS).document(assetDocId).update(assetData).await()
            Log.d("AssetRepo", "nkp update result $result")
            if(assetOwnerName.isNotEmpty()) {
                val assetHistory = hashMapOf(
                    ASSET_DOC_ID to assetDocId,
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
                assetDocId,
                assetName,
                assetType,
                modelName,
                serialNumber,
                description,
                selectedOwner,
                resultUri,
                assetId,
                assetQuantity,
                projectName,
                assetWorkingStatus
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
                    asset.assetDocId = document.id
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
                    asset.assetDocId = document.id
                    assetList.add(asset)
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return assetList
    }

    override suspend fun isAssetEditablePermission(): Boolean {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var isAssetEditablePermission = false
        val querySnapshot = db.collection(COLLECTION_MEMBERS).whereEqualTo(EMAIL_ADDRESS, currentUserEmail).get().await()
        try {
            for (document in querySnapshot.documents) {
                val member = document.toObject(Member::class.java)
                if(member != null) {
                    isAssetEditablePermission = member.assetEditablePermission
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return isAssetEditablePermission
    }

    override suspend fun deleteAsset(assetDocId: String, onSuccess: () -> Unit) {
        db.collection(COLLECTION_ASSETS).document(assetDocId).delete()
        .addOnSuccessListener {
            db.collection(COLLECTION_ASSETS_HISTORY).whereEqualTo(ASSET_DOC_ID, assetDocId).get().addOnSuccessListener { querySnapshot ->
                if(!querySnapshot.isEmpty){
                    for((index, document) in querySnapshot.documents.withIndex()) {
                        document.reference.delete().addOnSuccessListener {
                            if( index == (querySnapshot.size().minus(1)) ) {
                                onSuccess()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.w("AssetRepositoryImp", "nkp Error deleting assetHistory by assetDocId: ${e.message}")
                        }
                    }
                } else {
                    onSuccess()
                }
            }
        }
        .addOnFailureListener { e ->
            // Handle failure
            Log.w("AssetRepositoryImp", "nkp Error deleting Asset", e)
        }
    }

}