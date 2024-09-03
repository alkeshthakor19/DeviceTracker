package com.devicetracker.ui.dashbord.assets

import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextFieldDefaults.outlinedTextFieldColors
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberImagePainter
import com.devicetracker.R
import com.devicetracker.noRippleClickable
import com.devicetracker.singleClick
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.AssetNameField
import com.devicetracker.ui.components.AssetNameState
import com.devicetracker.ui.components.AssetTypeField
import com.devicetracker.ui.components.ModelDropdown

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
            val newAssetViewModel: NewAssetViewModel = hiltViewModel()
            AddAsset(
                onAssetSaved = { imageUri, imageBitmap, assetName, assetType, model ->
                    newAssetViewModel.uploadImageAndAddNewAssetToFirebase(
                        imageUri,
                        imageBitmap,
                        assetName,
                        assetType,
                        model,
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
    onAssetSaved: (imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, model: String) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    val assetNameState = remember { AssetNameState() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedAssetType by remember { mutableStateOf(AssetType.TAB) }
    var selectedModel by remember { mutableStateOf("") }

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
                onAssetSaved(imageUri, imageBitmap, assetNameState.text, selectedAssetType.name, selectedModel)
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
                    modifier = imageModifier
                )
            } ?: imageUri?.let {
                Image(
                    painter = rememberImagePainter(it),
                    contentDescription = null,
                    modifier = imageModifier
                )
            } ?: Image(
                painter = painterResource(id = R.drawable.ic_person),
                contentDescription = "Profile Picture",
                modifier = imageModifier
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

        Spacer(modifier = Modifier.height(8.dp))
        AssetNameField(assetName = assetNameState)

        Spacer(modifier = Modifier.height(8.dp))
        AssetTypeField(selectedAssetType = selectedAssetType, onAssetTypeSelected = { assetType ->
            selectedAssetType = assetType
        })

        Spacer(modifier = Modifier.height(8.dp))
        ModelDropdown(
            selectedAssetType = selectedAssetType,
            selectedModel = selectedModel,
            onModelSelected = { selectedModel = it }
        )

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

        Spacer(modifier = Modifier.height(16.dp))

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


