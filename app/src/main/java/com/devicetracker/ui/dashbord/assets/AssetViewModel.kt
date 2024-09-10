package com.devicetracker.ui.dashbord.assets

import android.graphics.Bitmap
import android.net.Uri
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

    var isLoaderShowing by mutableStateOf(true)
        private set

    private var updateAssetResponse by mutableStateOf<AddAssetResponse>(Response.Success(false))

    fun uploadImageAndAddNewAssetToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        assetName: String,
        assetType: String,
        modelName: String,
        serialNumber: String,
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
            modelName,
            serialNumber,
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

    fun fetchAssetDetailById(assetId: String) = liveData(Dispatchers.IO) {
        emit(getAssetDetailById(assetId))
    }

    private suspend fun getAssetDetailById(assetId: String) : GetAssetsByIdResponse {
        isLoaderShowing = true
        val result = repo.getAssetsDetailById(assetId)
        isLoaderShowing = false
        return result
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

    // Get all asset list by particular asset owner
    fun getAssetListByMemberId(memberId: String) = liveData(Dispatchers.IO) {
        emit(fetchAssetListByMemberId(memberId))
    }

    suspend fun fetchAssetListByMemberId(memberId: String): List<Asset> {
        isLoaderShowing = true
        val result = repo.getAssetListByMemberId(memberId)
        isLoaderShowing = false
        return result
    }

    // Function to upload image and update the asset in Firebase
    fun uploadImageAndUpdateAsset(assetId: String, isNeedToUpdateImageUrl: Boolean, imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, assetModelName: String, serialNumber: String, description: String, selectedOwner: Member?, onNavUp: () -> Unit) = viewModelScope.launch {
        updateAssetResponse = Response.Loading
        updateAssetResponse = repo.uploadImageAndUpdateAsset(
            assetId,
            isNeedToUpdateImageUrl,
            imageUri,
            imageBitmap,
            assetName,
            assetType,
            assetModelName,
            serialNumber,
            description,
            selectedOwner,
            onNavUp
        )
    }

    companion object {
        private const val TAG = "NewAssetViewModel"
    }
}