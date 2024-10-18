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
import androidx.compose.material.icons.filled.Add
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
import com.devicetracker.core.Constants.INT_SIZE_130
import com.devicetracker.core.Constants.INT_SIZE_170
import com.devicetracker.core.Constants.UNASSIGN_ID
import com.devicetracker.core.Constants.UNASSIGN_NAME
import com.devicetracker.singleClick
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
import com.devicetracker.ui.dashbord.member.MemberSaver
import com.devicetracker.ui.dashbord.member.MemberViewModel
import com.devicetracker.ui.isLandScapeMode

@Composable
fun NewAssetScreen(onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = stringResource(id = R.string.str_add_asset), onNavUp = onNavUp )
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
                onAssetSaved = { imageUri, imageBitmap, assetName, assetType, model, serialNumber, description, selectedMember, assetId, assetQuantity, projectName, assetWorkingStatus->
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
                        assetQuantity,
                        projectName,
                        assetWorkingStatus,
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
    onAssetSaved: (imageUri: Uri?, imageBitmap: Bitmap?, assetName: String, assetType: String, modelName: String, serialNumber : String, description: String, memberViewModel : Member, assetId: String, assetQuantity: String, projectName: String, assetWorkingStatus: Boolean) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    val memberViewModel : MemberViewModel = hiltViewModel()
    val members by memberViewModel.members.observeAsState(emptyList())
    // Use rememberSaveable for the additional state
    val noOwnerMember by rememberSaveable(stateSaver = MemberSaver) {
        mutableStateOf(Member(memberId = UNASSIGN_ID, memberName = UNASSIGN_NAME))
    }

    // Combine the local state with observed state
    val memberList = remember(members, noOwnerMember) {
        mutableListOf(noOwnerMember).apply {
            addAll(members)
        }
    }
    LaunchedEffect(Unit){
        memberViewModel.refreshMembers()
    }
    val assetNameState by rememberSaveable(stateSaver = AssetNameStateSaver) {  mutableStateOf(AssetNameState()) }
    Log.d("AssetNew", "nkp state : ${assetNameState.text}")

    var imageUri by rememberSaveable { mutableStateOf<Uri?>(null) }
    var imageBitmap by rememberSaveable { mutableStateOf<Bitmap?>(null) }
    var showImagePickDialog by rememberSaveable { mutableStateOf(false) }
    var selectedAssetType by rememberSaveable { mutableStateOf(AssetType.TAB) }
    var selectedModel by rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    val description by rememberSaveable(stateSaver = AssetDescriptionStateSaver) { mutableStateOf(AssetDescriptionState()) }
    val serialNumberState by rememberSaveable(stateSaver = AssetSerialNumberStateSaver) { mutableStateOf(AssetSerialNumberState()) }
    var selectedOwner by rememberSaveable(stateSaver = MemberSaver) { mutableStateOf(memberList.first()) }
    val assetIdState by rememberSaveable(stateSaver = AssetIdStateSaver) { mutableStateOf(AssetIdState()) }
    val assetQuantityState by rememberSaveable(stateSaver = AssetQuantityStateSaver) { mutableStateOf(AssetQuantityState()) }
    var selectedProjectName by rememberSaveable { mutableStateOf(Constants.EMPTY_STR) }
    var assetWorkingStatus by rememberSaveable { mutableStateOf(true) }

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
    val onAddNewAssetInAction = {
        if (!assetNameState.isValid) {
            assetNameState.enableShowError()
        } else if (selectedModel.isEmpty()) {
            // Show error or handle model not selected
        } else {
            onAssetSaved(imageUri, imageBitmap, assetNameState.text, selectedAssetType.name, selectedModel, serialNumberState.text, description.text, selectedOwner, assetIdState.text, assetQuantityState.text, selectedProjectName, assetWorkingStatus)
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
                (INT_SIZE_170 *1.7).toInt()
            } else {
                INT_SIZE_170
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
                        contentScale = ContentScale.FillBounds
                    )
                } ?: Image(
                    painter = painterResource(id = R.drawable.ic_devices),
                    contentDescription = "Profile Picture",
                    modifier = imageModifier,
                    contentScale = ContentScale.FillBounds
                )

                FloatingActionButton(
                    onClick = { showImagePickDialog = true },
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
            ProjectDropdown(selectedProjectName = selectedProjectName, onProjectSelected = {selectedProjectName = it})
            AssetStatusRadioButtons(
                assetWorkingStatus = assetWorkingStatus,
                onStatusChange = { status -> assetWorkingStatus = status }
            )
            AssetDescriptionField(
                description = description
            )
            Spacer(modifier = Modifier.height(15.dp))

        }
        ImagePickUpDialog(
            title = stringResource(id = R.string.str_choose_image),
            message = stringResource(id = R.string.str_image_pickup_message),
            isDialogOpen = showImagePickDialog,
            onDismiss = { showImagePickDialog = false },
            onCamera = { cameraPicker.launch(null) },
            onGallery = { galleryPicker.launch("image/*") }
        )
        // Save Button
        Button(
            modifier = Modifier
                .padding(vertical = 10.dp)
                .width(200.dp)
                .align(Alignment.BottomCenter),
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


