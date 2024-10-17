package com.devicetracker.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import com.devicetracker.core.Constants

open class BooleanFieldState(
    private val validator: (Boolean) -> Boolean = {true},
    private val errorFor:(String) -> String = { Constants.EMPTY_STR}
) {

    var isChecked: Boolean? by mutableStateOf(null)

    var errorFieldName: String by mutableStateOf(Constants.EMPTY_STR)

    var isFocused: Boolean by mutableStateOf(false)

    var isFocusedDirty: Boolean by mutableStateOf(false)

    var displayError: Boolean by mutableStateOf(false)


    open val isValid: Boolean
        get() = validator(isChecked==true)


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

val BooleanFieldStateSaver = Saver<BooleanFieldState, Map<String, Any>>(
    save = { state ->
        mapOf(
            "isChecked" to (state.isChecked==true),
            "errorFieldName" to state.errorFieldName,
            "isFocused" to state.isFocused,
            "isFocusedDirty" to state.isFocusedDirty,
            "displayError" to state.displayError
        )
    },
    restore = { map ->
        BooleanFieldState().apply {
            isChecked = map["isChecked"] as Boolean
            errorFieldName = map["errorFieldName"] as String
            isFocused = map["isFocused"] as Boolean
            isFocusedDirty = map["isFocusedDirty"] as Boolean
            displayError = map["displayError"] as Boolean
        }
    }
)
