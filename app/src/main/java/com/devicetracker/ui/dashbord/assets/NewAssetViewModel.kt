package com.devicetracker.ui.dashbord.assets

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.devicetracker.domain.models.Response
import com.devicetracker.domain.repository.AddAssetResponse
import com.devicetracker.domain.repository.AssetRepository
import com.devicetracker.domain.repository.GetAssetsByIdResponse
import com.devicetracker.domain.repository.GetAssetsResponse
import com.devicetracker.domain.repository.GetMembersResponse
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewAssetViewModel @Inject constructor(
    private val repo: AssetRepository
) :  ViewModel() {
    private val _asset = MutableLiveData<Asset>()
    val asset: LiveData<Asset> = _asset

    var addedAssetResponse by mutableStateOf<AddAssetResponse>(Response.Success(false))
        private set

    var isLoaderShowing by mutableStateOf<Boolean>(true)
        private set

    var getAssetsByIdResponse by mutableStateOf<GetAssetsByIdResponse>(Response.Success(null))
        private set

    fun addNewAsset(assetName: String, assetType: String, model: String, imageUrl: String) = viewModelScope.launch {
        addedAssetResponse = Response.Loading
        addedAssetResponse = repo.addAsset(assetName, assetType, model, imageUrl)
    }

    fun uploadImageAndAddNewAssetToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        model: String,
        onNavUp: () -> Unit
    ) = viewModelScope.launch {
        addedAssetResponse = Response.Loading
        addedAssetResponse = repo.uploadImageAndAddNewAssetToFirebase(
            imageUri,
            imageBitmap,
            assetName,
            assetType,
            model,
            onNavUp
        )
    }

    val assets = liveData(Dispatchers.IO) {
        emit(fetchAssets())
    }

    suspend fun fetchAssets(): GetAssetsResponse {
        isLoaderShowing = true
        val result = repo.getAssetsFromFirebase()
        isLoaderShowing = false
        return result
    }

    fun refreshAssets() = liveData(Dispatchers.IO) {
        emit(fetchAssets())
    }

/*
    fun fetchAssets() = viewModelScope.launch {
        isLoaderShowing = true
        getAssetResponse = repo.getAssetsFromFirebase()
        if(getAssetResponse is Response.Success){
            try {
                val querySnapshot = (getAssetResponse as Response.Success<QuerySnapshot?>).data
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val assets = querySnapshot.documents.map { document ->
                        val asset = document.toObject(Asset::class.java) ?: Asset()
                        asset.assetId = document.id
                        asset
                    }
                    _assets.value = assets
                    isLoaderShowing = false
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error data fetching : ${e.printStackTrace()}")
            }
        }
    }
*/
    fun getAssetDetailById(assetId: String) = viewModelScope.launch {
        isLoaderShowing = true
        getAssetsByIdResponse = repo.getAssetsDetailById(assetId)
        if(getAssetsByIdResponse is Response.Success){
            try {
                val documentSnapshot = (getAssetsByIdResponse as Response.Success<DocumentSnapshot?>).data
                if (documentSnapshot != null) {
                    val asset = documentSnapshot.toObject(Asset::class.java) ?: Asset()
                    asset.assetId = documentSnapshot.id
                    isLoaderShowing = false
                    _asset.value = asset
                }
            } catch (e: Exception) {
                Log.e("Asset", "Error data fetching : ${e.printStackTrace()}")
            }
        }
    }

    companion object {
        private const val TAG = "NewAssetViewModel"
    }
}