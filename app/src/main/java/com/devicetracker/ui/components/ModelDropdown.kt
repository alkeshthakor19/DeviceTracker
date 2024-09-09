package com.devicetracker.ui.components

import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import com.devicetracker.ui.dashbord.assets.AssetType
import com.devicetracker.ui.dashbord.assets.getModelsForAssetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModelDropdown(
    selectedAssetType: String,
    selectedModel: String,
    onModelSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val models = getModelsForAssetType(selectedAssetType)

    if (selectedAssetType == AssetType.OTHER.name) {
        OutlinedTextField(
            value = selectedModel,
            onValueChange = onModelSelected,
            label = { Text("Enter Model") },
            modifier = Modifier
        )
    } else {
        // Display the dropdown menu for other asset types
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedModel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Model") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                modifier = Modifier
                    .menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                models.forEach { model ->
                    DropdownMenuItem(
                        text = { Text(model) },
                        onClick = {
                            onModelSelected(model)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
