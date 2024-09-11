package com.devicetracker.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.devicetracker.core.Constants

open class BooleanFieldState(
    private val validator: (Boolean) -> Boolean = {true},
    private val errorFor:(String) -> String = { Constants.EMPTY_STR}
) {

    var isChecked: Boolean by mutableStateOf(false)

    var errorFieldName: String by mutableStateOf(Constants.EMPTY_STR)

    var isFocused: Boolean by mutableStateOf(false)

    var isFocusedDirty: Boolean by mutableStateOf(false)

    private var displayError: Boolean by mutableStateOf(false)


    open val isValid: Boolean
        get() = validator(isChecked)


    fun onFocusChange(focused: Boolean) {
        isFocused = focused
        if(focused) isFocusedDirty = true
    }

    fun enableShowError() {
        if(isFocusedDirty) {
            displayError = true
        }
    }


    fun showError() = !isValid && displayError

    open fun getError(): String? {
        return if(showError()) {
            errorFor(errorFieldName)
        } else null
    }
}
