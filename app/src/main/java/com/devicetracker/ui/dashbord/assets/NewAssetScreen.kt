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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import coil.compose.rememberAsyncImagePainter
import com.devicetracker.R
import com.devicetracker.core.Constants
import com.devicetracker.core.Constants.UNASSIGN_ID
import com.devicetracker.core.Constants.UNASSIGN_NAME
import com.devicetracker.noRippleClickable
import com.devicetracker.singleClick
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.AssetDescriptionField
import com.devicetracker.ui.components.AssetDescriptionState
import com.devicetracker.ui.components.AssetIdField
import com.devicetracker.ui.components.AssetIdState
import com.devicetracker.ui.components.AssetNameField
import com.devicetracker.ui.components.AssetNameState
import com.devicetracker.ui.components.AssetQuantityField
import com.devicetracker.ui.components.AssetQuantityState
import com.devicetracker.ui.components.AssetSerialNumberField
import com.devicetracker.ui.components.AssetSerialNumberState
import com.devicetracker.ui.components.AssetTypeSpinner
import com.devicetracker.ui.components.ModelDropdown
import com.devicetracker.ui.components.OwnerSpinner
import com.devicetracker.ui.components.ProjectNameField
import com.devicetracker.ui.components.ProjectNameState
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
                onAssetSaved = { imageUri, imageBitmap, assetName, assetType, model, serialNumber, description, selectedMember, assetId, assetQuatinty, projectName->
                    newAssetViewModel.uploadImageAndAddNewAssetToFirebase(
                        imageUri,
                        imageBitmap,
                        assetName,
                        assetType,
                        model,
                        serialNumber,
                        description,
                        selectedMember,
                        assetId,
                        assetQuatinty,
                        projectName,
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
fun AddAsset(
    onAssetSaved: (imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, modelName: String, serialNumber : String, description: String, memberViewModel : Member, assetId: String, assetQuantity: String, projectName: String) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    val memberViewModel : MemberViewModel = hiltViewModel()
    val members by memberViewModel.members.observeAsState(emptyList())
    val memberList = mutableListOf<Member>()
    val noOwnerMember = Member(memberId = UNASSIGN_ID, memberName = UNASSIGN_NAME)
    memberList.add(noOwnerMember)
    memberList.addAll(members)

    val assetNameState = remember { AssetNameState() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedAssetType by remember { mutableStateOf(AssetType.TAB) }
    var selectedModel by remember { mutableStateOf(Constants.EMPTY_STR) }
    val description = remember { AssetDescriptionState() }
    val serialNumberState = remember { AssetSerialNumberState() }
    var selectedOwner by remember { mutableStateOf(memberList.first()) }
    val assetIdState = remember { AssetIdState() }
    val assetQuantityState = remember { AssetQuantityState() }
    val projectNameState = remember { ProjectNameState() }

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
    val onAddNewAssetInAction = {
        if (!assetNameState.isValid) {
            assetNameState.enableShowError()
        } else if (selectedModel.isEmpty()) {
            // Show error or handle model not selected
        } else {
            onAssetSaved(imageUri, imageBitmap, assetNameState.text, selectedAssetType.name, selectedModel, serialNumberState.text, description.text, selectedOwner, assetIdState.text, assetQuantityState.text, projectNameState.text)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .noRippleClickable {
                    focusManager.clearFocus()
                    keyboardController?.hide()
                }
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(130.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
                    .align(Alignment.CenterHorizontally)
            ) {
                val imageModifier = Modifier.fillMaxSize()

                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.FillBounds
                    )
                } ?: imageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.Crop
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.ic_devices),
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
            AssetIdField(assetId = assetIdState)
            AssetTypeSpinner(selectedAssetType = selectedAssetType.toString(), onAssetTypeSelected = { assetType ->
                selectedAssetType = assetType
                selectedModel = Constants.EMPTY_STR
            })
            ModelDropdown(
                selectedAssetType = selectedAssetType.toString(),
                selectedModel = selectedModel,
                onModelSelected = { selectedModel = it }
            )
            AssetSerialNumberField(assetSerialNumber = serialNumberState)
            OwnerSpinner(memberList = memberList, selectedOwner = selectedOwner) {
                selectedOwner = it
            }
            AssetQuantityField(quantity = assetQuantityState)
            ProjectNameField(projectName = projectNameState)
            AssetDescriptionField(
                description = description
            )
            Spacer(modifier = Modifier.height(15.dp))
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
        }
        // Save Button
        Button(
            modifier = Modifier.padding(vertical = 10.dp).width(200.dp).align(Alignment.BottomCenter),
            shape = RoundedCornerShape(5.dp),
            onClick = singleClick {
                onAddNewAssetInAction()
                keyboardController?.hide()
            }
        ) {
            Text(stringResource(id = R.string.str_save))
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


