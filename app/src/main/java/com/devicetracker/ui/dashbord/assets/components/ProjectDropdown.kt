package com.devicetracker.ui.dashbord.assets.components

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import com.devicetracker.ui.CustomDropDownMenu
import com.devicetracker.ui.dashbord.assets.AssetViewModel

@Composable
fun ProjectDropdown(
    selectedProjectName: String,
    onProjectSelected: (String) -> Unit,
    viewModel: AssetViewModel = hiltViewModel(),
    isEditable: Boolean = true
) {
    val expanded = remember { mutableStateOf(false) }
    val projects by viewModel.projects.observeAsState(emptyList())

    LaunchedEffect(Unit) {
        viewModel.fetchProjects()
    }

    CustomDropDownMenu(
        label = "Project Name",
        selectedString = selectedProjectName,
        isEditable = isEditable,
        itemList = projects,
        isExpanded = expanded,
        itemToString = {
            it.name
        },
        onSelectedItem = {
            onProjectSelected(it.name)
        }
    )
}