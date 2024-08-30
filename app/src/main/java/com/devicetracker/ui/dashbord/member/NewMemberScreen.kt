package com.devicetracker.ui.dashbord.member

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.devicetracker.R
import com.devicetracker.noRippleClickable
import com.devicetracker.singleClick
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.CheckBoxState
import com.devicetracker.ui.components.EmailField
import com.devicetracker.ui.components.EmailState
import com.devicetracker.ui.components.EmployeeCodeField
import com.devicetracker.ui.components.EmployeeIdState
import com.devicetracker.ui.components.MemberNameField
import com.devicetracker.ui.components.MemberNameState
import com.devicetracker.ui.components.MemberTypeCheckBox
import com.devicetracker.ui.theme.AssetTrackerTheme

@Composable
fun NewMemberScreen(onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = "New Member", onNavUp )
        },
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotation ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                },
        ) {
            val newMemberViewModel: NewMemberViewModel = hiltViewModel()
            AddMember(
                onMemberSaved = { imageUri, imageBitmap, employeeId, memberName, memberEmail, isMemberWritablePermission ->
                    newMemberViewModel.uploadImageAndAddNewMemberToFirebase(
                        imageUri,
                        imageBitmap,
                        employeeId,
                        memberName,
                        memberEmail,
                        isMemberWritablePermission,
                        onNavUp
                    )
                },
                focusManager = focusManager,
                keyboardController = keyboardController
            )
        }
    }
}

@Composable
fun AddMember(
    onMemberSaved: (imageUri: Uri?, imageBitmap: Bitmap?, employeeId: Int, memberName: String, memberEmail: String, isMemberWritablePermission: Boolean) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    val emailState = remember { EmailState() }
    val employeeCodeState = remember { EmployeeIdState() }
    val memberNameState = remember { MemberNameState() }
    val memberWritablePermission = remember { CheckBoxState() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            imageBitmap = null
        }
    )

    val cameraPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            imageBitmap = bitmap
            imageUri = null
        }
    )

    Column(
        modifier = Modifier
            .noRippleClickable {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val onAddNewMemberInAction = {
            if(!employeeCodeState.isValid) {
                employeeCodeState.enableShowError()
            } else if(!memberNameState.isValid) {
                memberNameState.enableShowError()
            } else if(!emailState.isValid) {
                emailState.enableShowError()
            } else {
                Log.d("NewMemberScreen", "nkp imageUri ${imageUri?.path}  ${imageUri}")
                onMemberSaved(imageUri, imageBitmap, employeeCodeState.text.toInt(), memberNameState.text, emailState.text,memberWritablePermission.isChecked)
            }
        }

        /*val onAddNewMemberInAction = {
            if(employeeCodeState.isValid && memberNameState.isValid && emailState.isValid) {
                Log.d("NewMemberScreen", "nkp imageUri ${imageUri?.path}  ${imageUri}")
                onMemberSaved(imageUri, imageBitmap, employeeCodeState.text.toInt(), memberNameState.text, emailState.text,memberWritablePermission.isChecked)
                imageUri = null
                imageBitmap = null
                employeeCodeState.text = ""
                emailState.text = ""
                memberNameState.text = ""
            }
        }*/
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)
                .align(Alignment.CenterHorizontally)
        ) {
            val imageModifier = Modifier.fillMaxSize()

            imageBitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } ?: imageUri?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = null,
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Profile Picture",
                modifier = imageModifier,
                contentScale = ContentScale.Crop
            )

            FloatingActionButton(
                onClick = { showMenu = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(32.dp),
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add Image")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        EmployeeCodeField(employeeCodeState)
        MemberNameField(memberNameState)
        EmailField(emailState)
        MemberTypeCheckBox(memberWritablePermission)
        if (showMenu){
            ImagePickDialog(
                {
                    showMenu = false
                },
                onCamera = {
                    cameraPicker.launch(null)
                    showMenu = false
                },
                onGallery = {
                    galleryPicker.launch("image/*")
                    showMenu = false
                },
                "Choose Image",
                "Please select image from Gallery or Camera"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = singleClick{
                    onAddNewMemberInAction()
                    keyboardController?.hide()
                }) {
                Text(stringResource(id = R.string.str_save))
            }
        }
    }
}

@Composable
fun ImagePickDialog(
    onDismissRequest: () -> Unit,
    onCamera: () -> Unit,
    onGallery: () -> Unit,
    dialogTitle: String,
    dialogText: String
) {
    AlertDialog(
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onCamera()
                }
            ) {
                Text("Camera")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onGallery()
                }
            ) {
                Text("Gallery")
            }
        }
    )
}

@Preview
@Composable
private fun AddUserOrAssetPreview() {
    AssetTrackerTheme {
        NewMemberScreen(onNavUp = {})
    }
}