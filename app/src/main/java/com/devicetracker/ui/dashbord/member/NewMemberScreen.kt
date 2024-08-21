package com.devicetracker.ui.dashbord.member

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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.RotateLeft
import androidx.compose.material.icons.filled.RotateRight
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Severity
import coil.compose.rememberImagePainter
import com.devicetracker.R
import com.devicetracker.ui.TopBarWithTitleAndBackNavigation
import com.devicetracker.ui.home.isValidEmail
import com.devicetracker.ui.home.isValidEmployeeId
import com.devicetracker.ui.home.isValidUserName
import com.devicetracker.ui.theme.DeviceTrackerTheme

@Composable
fun NewMember(onNavUp: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopBarWithTitleAndBackNavigation(titleText = "New Member", onNavUp)
        },
    ) { paddingValues: PaddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, rotation ->
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                }
        ) {
            AddUser(
                onUserSaved = { onNavUp() },
                focusManager = focusManager,
                keyboardController = keyboardController
            )
        }
    }
}

@Composable
fun AddUser(
    onUserSaved: () -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    var employeeId by remember { mutableStateOf(TextFieldValue()) }
    var memberName by remember { mutableStateOf(TextFieldValue()) }
    var emailId by remember { mutableStateOf(TextFieldValue()) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showMenu by remember { mutableStateOf(false) }
    var isEditingImage by remember { mutableStateOf(false) }
    var imageOffset by remember { mutableStateOf(Offset.Zero) }
    var imageScale by remember { mutableStateOf(1f) }
    var imageRotation by remember { mutableStateOf(0f) }
    var employeeIdError by remember { mutableStateOf<String?>(null) }
    var userNameError by remember { mutableStateOf<String?>(null) }
    var emailIdError by remember { mutableStateOf<String?>(null) }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri: Uri? ->
            imageUri = uri
            imageBitmap = null
            isEditingImage = true
        }
    )

    val cameraPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview(),
        onResult = { bitmap ->
            imageBitmap = bitmap
            imageUri = null
            isEditingImage = true
        }
    )

    val focusRequester = remember { FocusRequester() }

    fun validateEmployeeId(value: String) {
        employeeIdError = if (isValidEmployeeId(value)) null else "Employee ID must be an integer"
    }

    fun validateUserName(value: String) {
        userNameError = if (isValidUserName(value)) null else "User Name must contain only characters"
    }

    fun validateEmail(value: String) {
        emailIdError = if (isValidEmail(value)) null else "Invalid email format or domain"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                focusManager.clearFocus()
                keyboardController?.hide()
            }
            .padding(16.dp),
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
            val imageModifier = if (isEditingImage) {
                Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = imageScale,
                        scaleY = imageScale,
                        translationX = imageOffset.x,
                        translationY = imageOffset.y,
                        rotationZ = imageRotation
                    )
                    .pointerInput(Unit) {
                        detectTransformGestures { _, pan, zoom, rotation ->
                            imageScale *= zoom
                            imageRotation += rotation
                            imageOffset = Offset(imageOffset.x + pan.x, imageOffset.y + pan.y)
                        }
                    }
            } else {
                Modifier.fillMaxSize()
            }

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

        Spacer(modifier = Modifier.height(16.dp))

        if (isEditingImage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { imageRotation -= 15f }) {
                    Icon(imageVector = Icons.Filled.RotateLeft, contentDescription = "Rotate Left")
                }
                IconButton(onClick = { imageRotation += 15f }) {
                    Icon(imageVector = Icons.Filled.RotateRight, contentDescription = "Rotate Right")
                }
                IconButton(onClick = { imageScale *= 1.1f }) {
                    Icon(imageVector = Icons.Filled.ZoomIn, contentDescription = "Zoom In")
                }
                IconButton(onClick = { imageScale /= 1.1f }) {
                    Icon(imageVector = Icons.Filled.ZoomOut, contentDescription = "Zoom Out")
                }
            }
        }

        OutlinedTextField(
            value = employeeId,
            onValueChange = {
                employeeId = it
                validateEmployeeId(it.text)
            },
            label = { Text("Employee ID") },
            isError = employeeIdError != null,
            keyboardActions = KeyboardActions {  ImeAction.Next },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
            trailingIcon = {
                if (employeeIdError != null) {
                    Icon(imageVector = Severity.Error, contentDescription = "Error")
                }
            }
        )
        employeeIdError?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = memberName,
            onValueChange = {
                memberName = it
                validateUserName(it.text)
            },
            label = { Text("Member Name") },
            isError = userNameError != null,
            keyboardActions = KeyboardActions {  ImeAction.Next },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
            trailingIcon = {
                if (userNameError != null) {
                    Icon(imageVector = Severity.Error, contentDescription = "Error")
                }
            }
        )
        userNameError?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        OutlinedTextField(
            value = emailId,
            onValueChange = {
                emailId = it
                validateEmail(it.text)
            },
            label = { Text("Email ID") },
            isError = emailIdError != null,
            keyboardActions = KeyboardActions {  ImeAction.Done },
            modifier = Modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.show()
                    }
                },
            trailingIcon = {
                if (emailIdError != null) {
                    Icon(imageVector = Icons.Filled.Error, contentDescription = "Error")
                }
            }
        )
        emailIdError?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("Capture Image") },
                onClick = {
                    showMenu = false
                    cameraPicker.launch(null)
                }
            )
            DropdownMenuItem(
                text = { Text("Select from Gallery") },
                onClick = {
                    showMenu = false
                    galleryPicker.launch("image/*")
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(onClick = { onUserSaved() }) {
                Text("Save")
            }
        }
    }
}

fun Icon(imageVector: Severity, contentDescription: String) {
    TODO("Not yet implemented")
}

@Preview
@Composable
private fun AddUserOrAssetPreview() {
    DeviceTrackerTheme {
        NewMember(onNavUp = {})
    }
}