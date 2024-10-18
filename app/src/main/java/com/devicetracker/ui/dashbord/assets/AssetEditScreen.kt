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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.devicetracker.R
import com.devicetracker.core.Constants
import com.devicetracker.core.Constants.INT_SIZE_130
import com.devicetracker.core.Constants.UNASSIGN_ID
import com.devicetracker.core.Constants.UNASSIGN_NAME
import com.devicetracker.ui.ImagePickUpDialog
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.components.AssetDescriptionField
import com.devicetracker.ui.components.AssetDescriptionState
import com.devicetracker.ui.components.AssetDescriptionStateSaver
import com.devicetracker.ui.components.AssetIdField
import com.devicetracker.ui.components.AssetIdState
import com.devicetracker.ui.components.AssetIdStateSaver
import com.devicetracker.ui.components.AssetNameField
import com.devicetracker.ui.components.AssetNameState
import com.devicetracker.ui.components.AssetNameStateSaver
import com.devicetracker.ui.components.AssetQuantityField
import com.devicetracker.ui.components.AssetQuantityState
import com.devicetracker.ui.components.AssetQuantityStateSaver
import com.devicetracker.ui.components.AssetSerialNumberField
import com.devicetracker.ui.components.AssetSerialNumberState
import com.devicetracker.ui.components.AssetSerialNumberStateSaver
import com.devicetracker.ui.components.AssetStatusRadioButtons
import com.devicetracker.ui.components.AssetTypeSpinner
import com.devicetracker.ui.components.ModelDropdown
import com.devicetracker.ui.components.OwnerSpinner
import com.devicetracker.ui.dashbord.assets.components.ProjectDropdown
import com.devicetracker.ui.dashbord.member.Member
import com.devicetracker.ui.dashbord.member.MemberViewModel
import com.devicetracker.ui.isLandScapeMode

@Composable
fun AssetEditScreen(assetDocId: String, onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val assetViewModel: AssetViewModel = hiltViewModel()
    val assetData by assetViewModel.asset.observeAsState()
    val assetEditablePermission by assetViewModel.isAssetEditablePermission().observeAsState(false)

    LaunchedEffect(assetDocId) {
        assetViewModel.fetchAssetDetailById(assetDocId)
    }
    Scaffold(
            topBar = {
                TopBarWithTitleAndBackNavigation(titleText = stringResource(id = R.string.str_edit_asset), onNavUp = onNavUp)
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
                UpdateAsset(
                    onAssetSaved = { isNeedToUpdateImageUrl, isNeedToAddAssetOwnerHistory, imageUri, imageBitmap, assetName, assetType, assetModelName, serialNumber, description, selectedOwner, assetId, assetQuantity, projectName, assetWorkingStatus ->
                        assetViewModel.uploadImageAndUpdateAsset(
                            assetDocId,
                            isNeedToUpdateImageUrl,
                            isNeedToAddAssetOwnerHistory,
                            imageUri,
                            imageBitmap,
                            assetName,
                            assetType,
                            assetModelName,
                            serialNumber,
                            description,
                            selectedOwner,
                            assetId,
                            assetQuantity,
                            projectName,
                            assetWorkingStatus,
                            onNavUp
                        )
                    },
                    focusManager = focusManager,
                    keyboardController = keyboardController,
                    initialAssetData = assetData,
                    assetEditablePermission
                )
            }
        }
}

@Composable
fun UpdateAsset(
    onAssetSaved: (isNeedToUpdateImageUrl: Boolean, isNeedToAddAssetOwnerHistory: Boolean, imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, modelName: String, serialNumber: String, description: String, selectedOwner: Member?, assetId: String, assetQuantity: String, projectName: String, assetWorkingStatus: Boolean) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?,
    initialAssetData: Asset?,
    assetEditablePermission: Boolean
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

    // Observe the members from the ViewModel
    val memberViewModel: MemberViewModel = hiltViewModel()
    val members by memberViewModel.members.observeAsState(emptyList())

    // Prepare the member list and unassigned member
    val noOwnerMember = Member(memberId = UNASSIGN_ID, memberName = UNASSIGN_NAME)
    val memberList = remember(members) { listOf(noOwnerMember) + members }

    // State to hold the selected owner
    val selectedOwner = remember { mutableStateOf<Member?>(null) }

    // LaunchedEffect to refresh members
    LaunchedEffect(Unit) {
        memberViewModel.refreshMembers()
    }

    // Update selectedOwner when members change
    LaunchedEffect(members) {
        selectedOwner.value = when {
            initialAssetData?.assetOwnerId != null -> {
                memberList.find { it.memberId == initialAssetData.assetOwnerId } ?: noOwnerMember
            }
            else -> noOwnerMember
        }
    }

    val assetNameState by rememberSaveable(stateSaver = AssetNameStateSaver) {  mutableStateOf(AssetNameState()) }
    if((assetNameState.text.isBlank()) || assetNameState.text == initialAssetData?.assetName) {
        assetNameState.text = initialAssetData?.assetName ?: Constants.EMPTY_STR
    }
    val initImageUri = if(initialAssetData?.imageUrl != null) {
        Uri.parse(initialAssetData.imageUrl)
    } else{
        null
    }
    var isNeedToUpdateImageUrl by rememberSaveable { mutableStateOf(false) }
    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    if(imageUri==null || imageUri == Uri.parse(initialAssetData?.imageUrl)){
        imageUri = initImageUri
    }
    var imageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var showImagePickDialog by rememberSaveable { mutableStateOf(false) }

    val selectedAssetType = rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    Log.d("AssetEdit", "nkp selectedAssetType ${selectedAssetType.value} assetType ${initialAssetData?.assetType}")
    if((selectedAssetType.value.isBlank() && initialAssetData?.assetType != null) || selectedAssetType.value == initialAssetData?.assetType) {
        selectedAssetType.value = assetType
    }
    val selectedModel = rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    if((selectedModel.value.isBlank()) || selectedModel.value == initialAssetData?.modelName) {
        selectedModel.value = selectedModelName
    }
    val serialNumberState by rememberSaveable(stateSaver = AssetSerialNumberStateSaver) { mutableStateOf(AssetSerialNumberState()) }
    if((serialNumberState.text.isBlank()) || serialNumberState.text == initialAssetData?.serialNumber) {
        serialNumberState.text = initialAssetData?.serialNumber?:Constants.EMPTY_STR
    }

    val description by rememberSaveable(stateSaver = AssetDescriptionStateSaver) { mutableStateOf(AssetDescriptionState()) }
    if((description.text.isBlank()) || description.text == initialAssetData?.description) {
        description.text = initialAssetData?.description ?: Constants.EMPTY_STR
    }

    val assetIdState by rememberSaveable(stateSaver = AssetIdStateSaver) { mutableStateOf(AssetIdState()) }
    if((assetIdState.text.isBlank()) || assetIdState.text == initialAssetData?.assetId) {
        assetIdState.text = initialAssetData?.assetId ?: Constants.EMPTY_STR
    }
    val assetQuantityState by rememberSaveable(stateSaver = AssetQuantityStateSaver) { mutableStateOf(AssetQuantityState()) }
    if((assetQuantityState.text.isBlank()) || assetQuantityState.text == initialAssetData?.quantity) {
        assetQuantityState.text = initialAssetData?.quantity ?: Constants.EMPTY_STR
    }
    val selectedProjectName = rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    if((selectedProjectName.value.isBlank()) || selectedProjectName.value == initialAssetData?.projectName) {
        selectedProjectName.value = initialAssetData?.projectName ?: Constants.EMPTY_STR
    }

    var assetWorkingStatus by rememberSaveable { mutableStateOf<Boolean?>(null) }
    if((assetWorkingStatus == null) || assetWorkingStatus == initialAssetData?.assetWorkingStatus) {
        assetWorkingStatus = initialAssetData?.assetWorkingStatus ?: true
    }

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
    val onAddNewAssetInAction = {
        if (!assetNameState.isValid) {
            assetNameState.enableShowError()
        } else if (selectedModel.value.isEmpty()) {
            // Show error or handle model not selected
        } else {
            onAssetSaved(isNeedToUpdateImageUrl, (selectedOwner.value?.memberId != initialAssetData?.assetOwnerId), imageUri, imageBitmap, assetNameState.text,
                selectedAssetType.value, selectedModel.value, serialNumberState.text, description.text, selectedOwner.value,
                assetIdState.text, assetQuantityState.text, selectedProjectName.value, assetWorkingStatus==true
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .nestedScroll(nestedScrollConnection)
                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 64.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageSize = if (isLandScapeMode()) {
                (INT_SIZE_130 *1.7).toInt()
            } else {
                INT_SIZE_130
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .height(imageSize.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color.Gray)
                    .align(Alignment.CenterHorizontally)
            ) {
                val imageModifier = Modifier.fillMaxSize()
                val resourceId = when(selectedAssetType.value) {
                    AssetType.TAB.name -> R.drawable.ic_devices
                    AssetType.CABLE.name -> R.drawable.ic_baseline_cable
                    else -> R.drawable.ic_devices_other
                }

                imageBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = imageModifier,
                        contentScale = ContentScale.FillBounds
                    )
                } ?: AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imageUri)
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(resourceId),
                    error = painterResource(resourceId),
                    contentDescription = stringResource(R.string.app_name),
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxWidth()
                )
                if (assetEditablePermission) {
                    FloatingActionButton(
                        onClick = { showImagePickDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                            .size(32.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(imageVector = Icons.Filled.Edit, contentDescription = stringResource(id = R.string.str_edit_image))
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            AssetNameField(assetName = assetNameState, isEditable = assetEditablePermission)
            AssetIdField(assetId = assetIdState, isEditable = assetEditablePermission)
            AssetTypeSpinner(selectedAssetType = selectedAssetType.value, onAssetTypeSelected = { assetType ->
                selectedAssetType.value = assetType.name
                selectedModel.value = Constants.EMPTY_STR
            }, isEditable = assetEditablePermission)
            ModelDropdown(
                selectedAssetType = selectedAssetType.value,
                selectedModel = selectedModel.value,
                onModelSelected = { selectedModel.value = it }, isEditable = assetEditablePermission
            )
            AssetSerialNumberField(assetSerialNumber = serialNumberState, isEditable = assetEditablePermission)
            OwnerSpinner(memberList = memberList, selectedOwner = selectedOwner.value) {
                selectedOwner.value = it
            }
            AssetQuantityField(quantity = assetQuantityState, isEditable = assetEditablePermission)
            ProjectDropdown(selectedProjectName = selectedProjectName.value, onProjectSelected = {selectedProjectName.value = it}, isEditable = assetEditablePermission)
            AssetStatusRadioButtons(
                assetWorkingStatus = assetWorkingStatus == true,
                onStatusChange = { status -> assetWorkingStatus = status }
            )
            AssetDescriptionField(description = description, isEditable = assetEditablePermission)

            Spacer(modifier = Modifier.height(15.dp))

            ImagePickUpDialog(
                title = stringResource(id = R.string.str_choose_image),
                message = stringResource(id = R.string.str_image_pickup_message),
                isDialogOpen = showImagePickDialog, 
                onDismiss = { showImagePickDialog = false },
                onCamera = { cameraPicker.launch(null) }, 
                onGallery = { galleryPicker.launch("image/*") }
            )
        }
        // Save Button
        Button(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(200.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(5.dp),
            onClick = onAddNewAssetInAction
        ) {
            Text(stringResource(R.string.str_update))
        }
    }
}
