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
import com.devicetracker.domain.repository.GetAssignHistoriesResponse
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.firestore.DocumentSnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AssetViewModel @Inject constructor(
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

    fun addNewAsset(assetName: String, assetType: String, model: String, description: String, selectedMember: Member, imageUrl: String) = viewModelScope.launch {
        addedAssetResponse = Response.Loading
        addedAssetResponse = repo.addAsset(assetName, assetType, model, description, selectedMember, imageUrl)
    }

    fun uploadImageAndAddNewAssetToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        model: String,
        description: String,
        selectedMember: Member,
        onNavUp: () -> Unit
    ) = viewModelScope.launch {
        addedAssetResponse = Response.Loading
        addedAssetResponse = repo.uploadImageAndAddNewAssetToFirebase(
            imageUri,
            imageBitmap,
            assetName,
            assetType,
            model,
            description,
            selectedMember,
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

    fun getAssetHistories(assetId: String) = liveData(Dispatchers.IO) {
        emit(fetchAssetHistories(assetId))
    }

    private suspend fun fetchAssetHistories(assetId: String): GetAssignHistoriesResponse {
        isLoaderShowing = true
        val result = repo.getPreviousAssignHistoriesByAssetId(assetId)
        isLoaderShowing = false
        return result
    }

    companion object {
        private const val TAG = "NewAssetViewModel"
    }
}