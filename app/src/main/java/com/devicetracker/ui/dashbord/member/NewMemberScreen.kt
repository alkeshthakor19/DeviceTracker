package com.devicetracker.ui.dashbord.member

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.devicetracker.R
import com.devicetracker.singleClick
import com.devicetracker.ui.ImagePickUpDialog
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.AssetEditableCheckBox
import com.devicetracker.ui.components.CheckBoxState
import com.devicetracker.ui.components.EmailField
import com.devicetracker.ui.components.EmailState
import com.devicetracker.ui.components.EmloyeeCodeState
import com.devicetracker.ui.components.EmployeeCodeField
import com.devicetracker.ui.components.MemberEditableCheckBox
import com.devicetracker.ui.components.MemberMobileField
import com.devicetracker.ui.components.MemberNameField
import com.devicetracker.ui.components.MemberNameState
import com.devicetracker.ui.components.MobileNumberState

@Composable
fun NewMemberScreen(onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = stringResource(id = R.string.str_new_member), onNavUp)
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
            val memberViewModel: MemberViewModel = hiltViewModel()
            AddMember(
                onMemberSaved = { imageUri, imageBitmap, employeeId, memberName, memberEmail, memberEditablePermission, assetEditablePermission, mobileNumber ->
                    memberViewModel.uploadImageAndAddNewMemberToFirebase(
                        imageUri,
                        imageBitmap,
                        employeeId,
                        memberName,
                        memberEmail,
                        memberEditablePermission,
                        assetEditablePermission,
                        mobileNumber,
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
    onMemberSaved: (imageUri: Uri?, imageBitmap: Bitmap?, employeeId: Int, memberName: String, memberEmail: String, memberEditablePermission: Boolean, assetEditablePermission: Boolean, mobileNumber: String) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    val emailState = remember { EmailState() }
    val employeeCodeState = remember { EmloyeeCodeState() }
    val memberNameState = remember { MemberNameState() }
    val mobileNumberState = remember { MobileNumberState() }
    val memberEditablePermission = remember { CheckBoxState() }
    val assetEditablePermission = remember { CheckBoxState() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showImagePickDialog by remember { mutableStateOf(false) }

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

    val scrollState = rememberScrollState()
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                focusManager.clearFocus()
                keyboardController?.hide()
                return Offset.Zero
            }
        }
    }
    val onAddNewMemberInAction = {
        if (!employeeCodeState.isValid) {
            employeeCodeState.enableShowError()
        } else if (!memberNameState.isValid) {
            memberNameState.enableShowError()
        } else if (!emailState.isValid) {
            emailState.enableShowError()
        } else {
            onMemberSaved(
                imageUri,
                imageBitmap,
                employeeCodeState.text.toInt(),
                memberNameState.text,
                emailState.text,
                memberEditablePermission.isChecked,
                assetEditablePermission.isChecked,
                mobileNumberState.text
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .padding(start = 32.dp, end = 32.dp, top = 16.dp, bottom = 64.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.ic_person),
                    contentDescription = stringResource(id = R.string.str_profile_picture),
                    modifier = imageModifier,
                    contentScale = ContentScale.Crop
                )

                FloatingActionButton(
                    onClick = { showImagePickDialog = true },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(32.dp),
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(imageVector = Icons.Filled.Add, contentDescription = stringResource(id = R.string.str_add_image))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            EmployeeCodeField(employeeCodeState)
            MemberNameField(memberNameState)
            EmailField(emailState)
            MemberMobileField(mobileNumberState)
            MemberEditableCheckBox(memberEditablePermission)
            AssetEditableCheckBox(assetEditablePermission)
            ImagePickUpDialog(
                title = stringResource(id = R.string.str_choose_image),
                message = stringResource(id = R.string.str_image_pickup_message),
                isDialogOpen = showImagePickDialog,
                onDismiss = { showImagePickDialog = false },
                onCamera = { cameraPicker.launch(null) },
                onGallery = { galleryPicker.launch("image/*") }
            )
        }

        Button(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(200.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(5.dp),
            onClick = singleClick {
                onAddNewMemberInAction()
                keyboardController?.hide()
            }) {
            Text(stringResource(id = R.string.str_save))
        }
    }
}
