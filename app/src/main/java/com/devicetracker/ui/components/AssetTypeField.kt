package com.devicetracker.ui.components


import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.devicetracker.ui.dashbord.assets.AssetType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetTypeField(
    selectedAssetType: String,
    onAssetTypeSelected: (AssetType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedAssetType,
            onValueChange = {},
            readOnly = true,
            label = { Text(text = "Asset Type") },
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
            AssetType.entries.forEach { assetType ->
                DropdownMenuItem(
                    text = { Text(assetType.name) },
                    onClick = {
                        onAssetTypeSelected(assetType)
                        expanded = false
                    }
                )
            }
        }
    }
}
