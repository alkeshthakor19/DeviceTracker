package com.devicetracker.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable

class CheckBoxState : BooleanFieldState(validator = ::isCheckBoxValid, errorFor = ::checkBoxValidationError)

private fun isCheckBoxValid(isChecked: Boolean): Boolean {
    return isChecked
}

private fun checkBoxValidationError(checkBoxName: String): String {
    return "Please checked."
}

@Composable
fun rememberCheckBoxState(): BooleanFieldState {
    return rememberSaveable(saver = BooleanFieldStateSaver) { CheckBoxState() }
}