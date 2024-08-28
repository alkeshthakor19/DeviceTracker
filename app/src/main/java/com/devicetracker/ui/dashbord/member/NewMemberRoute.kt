package com.devicetracker.ui.dashbord.member

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.ProgressBar

@Composable
fun AddMemberRoute(onNavUp: () -> Unit) {
    val context = LocalContext.current
    NewMemberScreen(onNavUp)
    LoaderShowHide(
        showErrorMessage = { errorMessage ->
            showMessage(context, errorMessage)
        }
    )
}

@Composable
fun LoaderShowHide(showErrorMessage: (errorMessage: String?) -> Unit) {
    val newMemberViewModel: NewMemberViewModel = hiltViewModel()
    val context = LocalContext.current
    when(val addedMemberResponse = newMemberViewModel.addedMemberResponse) {
        is Response.Loading -> ProgressBar()
        is Response.Success -> {
            val isAddedMember = addedMemberResponse.data
            if (isAddedMember) {
                showMessage(context, "Added new member successfully!!")
            }
            Unit
        }
        is Response.Failure -> addedMemberResponse.apply {
            LaunchedEffect(e) {
                print(e)
                showErrorMessage(e.message)
            }
        }
    }
}