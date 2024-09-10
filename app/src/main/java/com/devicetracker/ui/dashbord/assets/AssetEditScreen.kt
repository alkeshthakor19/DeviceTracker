package com.devicetracker.ui.dashbord.assets

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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.devicetracker.R
import com.devicetracker.core.Constants
import com.devicetracker.noRippleClickable
import com.devicetracker.ui.ProgressBar
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.AssetDescriptionField
import com.devicetracker.ui.components.AssetDescriptionState
import com.devicetracker.ui.components.AssetNameField
import com.devicetracker.ui.components.AssetNameState
import com.devicetracker.ui.components.AssetSerialNumberField
import com.devicetracker.ui.components.AssetSerialNumberState
import com.devicetracker.ui.components.AssetTypeField
import com.devicetracker.ui.components.ModelDropdown
import com.devicetracker.ui.components.OwnerSpinner
import com.devicetracker.ui.dashbord.member.Member
import com.devicetracker.ui.dashbord.member.MemberViewModel

@Composable
fun AssetEditScreen(assetId: String, onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val assetViewModel: AssetViewModel = hiltViewModel()
    val assetData by assetViewModel.fetchAssetDetailById(assetId).observeAsState()
    Scaffold(
            topBar = {
                TopBarWithTitleAndBackNavigation(titleText = "Edit Asset", onNavUp = onNavUp)
            }
        ) { paddingValues: PaddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .pointerInput(Unit) {
                        detectTransformGestures { _, _, _, _ ->
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    }
            ) {
                if(assetViewModel.isLoaderShowing){
                    ProgressBar()
                } else{
                    UpdateAsset(
                        onAssetSaved = { isNeedToUpdateImageUrl, imageUri, imageBitmap, assetName, assetType, assetModelName, serialNumber, description, selectedOwner ->
                            assetViewModel.uploadImageAndUpdateAsset(
                                assetId,
                                isNeedToUpdateImageUrl,
                                imageUri,
                                imageBitmap,
                                assetName,
                                assetType,
                                assetModelName,
                                serialNumber,
                                description,
                                selectedOwner,
                                onNavUp
                            )
                        },
                        focusManager = focusManager,
                        keyboardController = keyboardController,
                        initialAssetData = assetData
                    )
                }
            }
        }
}

@Composable
fun UpdateAsset(
    onAssetSaved: (isNeedToUpdateImageUrl: Boolean, imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, modelName: String, serialNumber: String, description: String, selectedOwner: Member?) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    initialAssetData: Asset?
) {
    val selectedModelName = if(initialAssetData?.modelName != null){
        initialAssetData.modelName
    } else {
        Constants.EMPTY_STR
    }
    val assetType = if(initialAssetData?.assetType != null){
        initialAssetData.assetType
    } else {
        AssetType.TAB.name
    }
    val memberViewModel: MemberViewModel = hiltViewModel()
    val members by memberViewModel.members.observeAsState(emptyList())
    val memberList = mutableListOf<Member>()
    val noOwnerMember = Member(memberId = "unassign", memberName = "No Owner")
    memberList.add(noOwnerMember)
    memberList.addAll(members)
    val initOwner = if(initialAssetData?.assetOwnerId != null && memberList.isNotEmpty()){
        memberList.find { it.memberId == initialAssetData.assetOwnerId }
    } else {
        memberList.first()
    }
    val selectedOwner = remember { mutableStateOf(initOwner)}

    Log.d("AssetEditScreen","nkp asset name: $initialAssetData")
    val assetNameState = remember { AssetNameState() }
    assetNameState.text = initialAssetData?.assetName ?: Constants.EMPTY_STR
    val initImageUri = if(initialAssetData?.imageUrl != null) {
        Uri.parse(initialAssetData.imageUrl)
    } else{
        null
    }
    var isNeedToUpdateImageUrl by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf(initImageUri) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }

    val selectedAssetType = remember { mutableStateOf(assetType) }
    val selectedModel = remember { mutableStateOf(selectedModelName) }
    val serialNumberState = remember { AssetSerialNumberState() }
    serialNumberState.text = initialAssetData?.serialNumber?:Constants.EMPTY_STR

    val description = remember { AssetDescriptionState() }
    description.text = initialAssetData?.description ?: Constants.EMPTY_STR

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            isNeedToUpdateImageUrl = true
            imageBitmap = null
        }
    )

    val cameraPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            imageBitmap = bitmap
            isNeedToUpdateImageUrl = true
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
            } else if (selectedModel.value.isEmpty()) {
                // Show error or handle model not selected
            } else {
                onAssetSaved(isNeedToUpdateImageUrl, imageUri, imageBitmap, assetNameState.text,
                    selectedAssetType.value, selectedModel.value, serialNumberState.text, description.text, selectedOwner.value
                )
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
                    painter = rememberAsyncImagePainter(it),
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
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit Image")
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
        AssetNameField(assetName = assetNameState)

        AssetTypeField(selectedAssetType = selectedAssetType.value, onAssetTypeSelected = { assetType ->
            Log.d("AssetEditScreen", "nkp1 call when change the assetType")
            selectedAssetType.value = assetType.name
            selectedModel.value = ""
        })
        ModelDropdown(
            selectedAssetType = selectedAssetType.value,
            selectedModel = selectedModel.value,
            onModelSelected = { selectedModel.value = it }
        )

        AssetSerialNumberField(assetSerialNumber = serialNumberState)

        OwnerSpinner(memberList = memberList, selectedOwner = selectedOwner.value) {
            Log.d("AssetEdit", "nkp selected click name ${it.memberName}")
            selectedOwner.value = it
        }

        AssetDescriptionField(description = description)

        Spacer(modifier = Modifier.height(8.dp))

        if (showMenu) {
            ImagePickDialog(
                onDismissRequest = { showMenu = false },
                onCamera = {
                    cameraPicker.launch(null)
                    showMenu = false
                },
                onGallery = {
                    galleryPicker.launch("image/*")
                    showMenu = false
                },
                dialogTitle = "Choose Image",
                dialogText = "Please select image from Gallery or Camera"
            )
        }

        // Save Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = onAddNewAssetInAction) {
                Text(text = "Update")
            }
        }
    }
}
