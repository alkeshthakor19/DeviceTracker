package com.devicetracker.ui.dashbord.assets

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.devicetracker.R
import com.devicetracker.noRippleClickable
import com.devicetracker.singleClick
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.AssetDescriptionField
import com.devicetracker.ui.components.AssetDescriptionState
import com.devicetracker.ui.components.AssetNameField
import com.devicetracker.ui.components.AssetNameState
import com.devicetracker.ui.components.AssetTypeField
import com.devicetracker.ui.components.ModelDropdown
import com.devicetracker.ui.components.OwnerSpinner
import com.devicetracker.ui.dashbord.member.Member
import com.devicetracker.ui.dashbord.member.MemberViewModel

@Composable
fun NewAssetScreen(onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = "New Asset", onNavUp = onNavUp )
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
            val newAssetViewModel: AssetViewModel = hiltViewModel()
            AddAsset(
                onAssetSaved = { imageUri, imageBitmap, assetName, assetType, model, description, selectedMember->
                    newAssetViewModel.uploadImageAndAddNewAssetToFirebase(
                        imageUri,
                        imageBitmap,
                        assetName,
                        assetType,
                        model,
                        description,
                        selectedMember,
                        onNavUp
                    )
                },
                focusManager = focusManager,
                keyboardController = keyboardController
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAsset(
    onAssetSaved: (imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, model: String, description: String, memberViewModel : Member) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    val memberViewModel : MemberViewModel = hiltViewModel()
    val members by memberViewModel.members.observeAsState(emptyList())
    val memberList = mutableListOf<Member>()
    val noOwnerMember = Member(memberId = "unassign", memberName = "No Owner")
    memberList.add(noOwnerMember)
    memberList.addAll(members)

    val assetNameState = remember { AssetNameState() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedAssetType by remember { mutableStateOf(AssetType.TAB) }
    var selectedModel by remember { mutableStateOf("") }
    val description = remember { AssetDescriptionState() }
    var selectedOwner by remember { mutableStateOf(memberList.first()) }

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
        val onAddNewAssetInAction = {
            if (!assetNameState.isValid) {
                assetNameState.enableShowError()
            } else if (selectedModel.isEmpty()) {
                // Show error or handle model not selected
            } else {
                onAssetSaved(imageUri, imageBitmap, assetNameState.text, selectedAssetType.name, selectedModel, description.text, selectedOwner)
            }
        }

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

        Spacer(modifier = Modifier.height(2.dp))
        AssetNameField(assetName = assetNameState)

        AssetTypeField(selectedAssetType = selectedAssetType, onAssetTypeSelected = { assetType ->
            selectedAssetType = assetType
            selectedModel = ""
        })

        ModelDropdown(
            selectedAssetType = selectedAssetType,
            selectedModel = selectedModel,
            onModelSelected = { selectedModel = it }
        )
        OwnerSpinner(memberList = memberList, selectedOwner = selectedOwner) {
            selectedOwner = it
        }
        AssetDescriptionField(
            description = description
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (showMenu) {
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


        // Save Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = singleClick {
                onAddNewAssetInAction()
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


