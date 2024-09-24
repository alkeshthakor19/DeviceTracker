package com.devicetracker.ui.components

class ProjectNameState : TextFieldState(validator = ::isProjectNameValid, errorFor = ::projectNameValidationError)

private fun isProjectNameValid(projectName: String): Boolean {
    return projectName.isNotEmpty()
}

private fun projectNameValidationError(projectName: String): String {
    return "Please enter project name."
}