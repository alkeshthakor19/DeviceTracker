package com.devicetracker.ui.components

class CheckBoxState : BooleanFieldState(validator = ::isCheckBoxValid, errorFor = ::checkBoxValidationError)

private fun isCheckBoxValid(isChecked: Boolean): Boolean {
    return isChecked
}

private fun checkBoxValidationError(checkBoxName: String): String {
    return "Please checked."
}