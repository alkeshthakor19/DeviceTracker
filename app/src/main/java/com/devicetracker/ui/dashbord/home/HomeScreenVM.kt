package com.devicetracker.ui.dashbord.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.devicetracker.domain.repository.AssetRepository
import com.devicetracker.domain.repository.AuthRepository
import com.devicetracker.ui.dashbord.assets.Asset
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class HomeScreenVM @Inject constructor(private val authRepo: AuthRepository, private val assetRepo: AssetRepository): ViewModel() {
    var isLoaderShowing by mutableStateOf(false)
        private set

    fun signOut() = authRepo.signOut()

    fun fetchAssetsByAssetType(assetType: String) = liveData(Dispatchers.IO) {
        emit(getAssetsByAssetType(assetType))
    }

    private suspend fun getAssetsByAssetType(assetType: String): List<Asset> {
        isLoaderShowing = true
        val result = assetRepo.getAssetsByAssetType(assetType)
        isLoaderShowing = false
        return result
    }
}