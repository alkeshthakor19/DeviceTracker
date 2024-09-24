package com.devicetracker.ui.dashbord.assets

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.R
import com.devicetracker.noRippleClickable
import com.devicetracker.singleClick
import com.devicetracker.ui.components.AssetDescriptionState
import com.devicetracker.ui.components.AssetModelField
import com.devicetracker.ui.components.AssetTypeSpinner
import com.devicetracker.ui.theme.AssetTrackerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetModelScreen(openDrawer: () -> Unit) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asset Model",  style = MaterialTheme.typography.headlineMedium, color = AssetTrackerTheme.colors.textColor) },
                navigationIcon = {
                    IconButton(onClick = openDrawer) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "Menu")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = Color.Black,
                ),
            )
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
            AddAssetModel(
                onAssetSaved = { assetType, model->
                    newAssetViewModel.addAssetModelToFirebase(assetType, model)
                },
                focusManager = focusManager,
                keyboardController = keyboardController
            )
        }
    }
}

@Composable
fun AddAssetModel(
    onAssetSaved: (assetType: String, model: String) -> Unit,
    focusManager: FocusManager,
    keyboardController: SoftwareKeyboardController?
) {
    var selectedAssetType by remember { mutableStateOf(AssetType.TAB) }
    val assetModel = remember { AssetDescriptionState() }

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
        val onAddNewAssetModelInAction = {
            if (!assetModel.isValid) {
                assetModel.enableShowError()
            } else {
                onAssetSaved(selectedAssetType.name, assetModel.text)
            }
        }

        AssetTypeSpinner(selectedAssetType = selectedAssetType.toString(), onAssetTypeSelected = { assetType ->
            selectedAssetType = assetType
        }, true)

        AssetModelField(model = assetModel)

        Spacer(modifier = Modifier.height(15.dp))
        // Save Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.width(200.dp),
                shape = RoundedCornerShape(5.dp),
                onClick = singleClick {
                    onAddNewAssetModelInAction()
                    keyboardController?.hide()
                }
            ) {
                Text(stringResource(id = R.string.str_save))
            }
        }
    }
}
