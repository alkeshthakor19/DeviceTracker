package com.devicetracker.ui.dashbord.assets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.ProgressBar

@Composable
fun AssetModelRoute(onNavUp: () -> Unit) {
    val context = LocalContext.current
    AssetModelScreen(onNavUp)
    LoaderShowHideForModel(
        showErrorMessage = { errorMessage ->
            showMessage(context, errorMessage)
        }
    )
}

@Composable
fun LoaderShowHideForModel(showErrorMessage: (errorMessage: String?) -> Unit) {
    val newAssetViewModel: AssetViewModel = hiltViewModel()
    val context = LocalContext.current
    when(val addedAssetModelResponse = newAssetViewModel.addedAssetModelResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val isAddedAsset = addedAssetModelResponse.data
            if (isAddedAsset) {
                showMessage(context, "Added model name successfully!!")
            }
            Unit
        }
        is Response.Failure -> addedAssetModelResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e.message)
            }
        }
    }
}