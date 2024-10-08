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
import com.devicetracker.domain.repository.AddModelResponse
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

    private val _assets = MutableLiveData<List<Asset>>()
    val assets: LiveData<List<Asset>> get() = _assets

    private val _asset = MutableLiveData<Asset>()
    val asset: LiveData<Asset> get() = _asset

    private val _assetHistories = MutableLiveData<List<AssetHistory>>()
    val assetHistories: LiveData<List<AssetHistory>> get() = _assetHistories

    private val _models = MutableLiveData<List<String>>()
    val models: LiveData<List<String>> = _models

    var addedAssetResponse by mutableStateOf<AddAssetResponse>(Response.Success(false))
        private set

    var addedAssetModelResponse by mutableStateOf<AddModelResponse>(Response.Success(false))
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
        assetId: String,
        assetQuantity: String,
        projectName: String,
        assetWorkingStatus: Boolean,
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
            assetId,
            assetQuantity,
            projectName,
            assetWorkingStatus,
            onNavUp
        )
    }

    fun addAssetModelToFirebase(assetType: String, model: String) = viewModelScope.launch {
        addedAssetModelResponse = Response.Loading
        addedAssetModelResponse = repo.addModel(assetType,model)
    }

    fun fetchModels(assetType: String) = viewModelScope.launch {
        _models.value = repo.getModelsForAssetType(assetType)
    }

    fun refreshAssets() {
        viewModelScope.launch(Dispatchers.IO) {
            val newAssets = fetchAssets()
            _assets.postValue(newAssets)
        }
    }

    fun refreshAssetsByAssetType(assetType: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val newAssets = getAssetsByAssetType(assetType)
            _assets.postValue(newAssets)
        }
    }

    private suspend fun getAssetsByAssetType(assetType: String): List<Asset> {
        isLoaderShowing = true
        val result = repo.getAssetsByAssetType(assetType)
        isLoaderShowing = false
        return result
    }

    private suspend fun fetchAssets(): GetAssetsResponse {
        isLoaderShowing = true
        val result = repo.getAssetsFromFirebase()
        isLoaderShowing = false
        return result
    }

    fun fetchAssetDetailById(assetDocId: String) {
        viewModelScope.launch {
            val newAsset = getAssetDetailById(assetDocId)
            _asset.postValue(newAsset)
        }
    }

    private suspend fun getAssetDetailById(assetDocId: String) : GetAssetsByIdResponse {
        isLoaderShowing = true
        val result = repo.getAssetsDetailById(assetDocId)
        isLoaderShowing = false
        return result
    }

    fun getAssetHistories(assetDocId: String) {
        viewModelScope.launch {
            val newAssetHistories = fetchAssetHistories(assetDocId)
            _assetHistories.postValue(newAssetHistories)
        }
    }

    private suspend fun fetchAssetHistories(assetDocId: String): GetAssignHistoriesResponse {
        isLoaderShowing = true
        val result = repo.getPreviousAssignHistoriesByAssetId(assetDocId)
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
    fun uploadImageAndUpdateAsset(assetDocId: String, isNeedToUpdateImageUrl: Boolean, imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, assetModelName: String, serialNumber: String, description: String, selectedOwner: Member?, assetId: String, assetQuantity: String, projectName: String, assetWorkingStatus: Boolean, onNavUp: () -> Unit) = viewModelScope.launch {
        updateAssetResponse = Response.Loading
        updateAssetResponse = repo.uploadImageAndUpdateAsset(
            assetDocId,
            isNeedToUpdateImageUrl,
            imageUri,
            imageBitmap,
            assetName,
            assetType,
            assetModelName,
            serialNumber,
            description,
            selectedOwner,
            assetId,
            assetQuantity,
            projectName,
            assetWorkingStatus,
            onNavUp
        )
    }

    fun isAssetEditablePermission() = liveData(Dispatchers.IO) {
        emit(getAssetEditablePermission())
    }

    private suspend fun getAssetEditablePermission(): Boolean {
        val result = repo.isAssetEditablePermission()
        return result
    }

    /**
     * For Delete the asset details by assetDocId
     *
     * @param assetDocId
     * @param onSuccess
     */
    fun deleteAssetByAssetDocId(assetDocId: String, onSuccess: () -> Unit) {
        isLoaderShowing = true
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteAsset(assetDocId){
                onSuccess()
                isLoaderShowing = false
            }
        }
    }

    companion object {
        private const val TAG = "NewAssetViewModel"
    }
}