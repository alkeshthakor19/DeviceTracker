package com.devicetracker.ui.dashbord.member

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.ProgressBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LoaderShowHide(showErrorMessage: (errorMessage: String?) -> Unit) {
    val memberViewModel: MemberViewModel = hiltViewModel()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    when(val addedMemberResponse = memberViewModel.addedMemberResponse) {
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