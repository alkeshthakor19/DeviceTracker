package com.devicetracker.ui.dashbord.assets

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.R
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.ProgressBar

@Composable
fun AddAssetRoute(onNavUp: () -> Unit) {
    val context = LocalContext.current
    NewAssetScreen(onNavUp)
    LoaderShowHide(
        showErrorMessage = { errorMessage ->
            showMessage(context, errorMessage)
        }
    )
}

@Composable
fun LoaderShowHide(showErrorMessage: (errorMessage: String?) -> Unit) {
    val newAssetViewModel: AssetViewModel = hiltViewModel()
    val context = LocalContext.current
    when(val addedAssetResponse = newAssetViewModel.addedAssetResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val isAddedAsset = addedAssetResponse.data
            if (isAddedAsset) {
                LaunchedEffect(isAddedAsset) {
                    showMessage(context, context.getString(R.string.str_asset_added_message))
                }
            }
            Unit
        }
        is Response.Failure -> addedAssetResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e.message)
            }
        }
    }
}