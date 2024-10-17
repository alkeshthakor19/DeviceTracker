package com.devicetracker.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import com.devicetracker.core.Constants

open class TextFieldState(
    private val validator: (String) -> Boolean = {true},
    private val errorFor:(String) -> String = { Constants.EMPTY_STR }
) {

    var text: String by mutableStateOf(Constants.EMPTY_STR)

    var isFocused: Boolean by mutableStateOf(false)

    var isFocusedDirty: Boolean by mutableStateOf(false)

    var displayError: Boolean by mutableStateOf(false)


    open val isValid: Boolean
        get() = text.isNotBlank() && validator(text)
    
    fun onFocusChange(focused: Boolean) {
        isFocused = focused
    }

    fun enableShowError() {
            displayError = true
    }


    fun showError() = !isValid && displayError

    open fun getError(fieldName: String): String? {
        return if(showError()) {
            if (text.isBlank()) "$fieldName can't be empty" else errorFor(text)
        } else null
    }
}

fun textFiledStateSaver(state: TextFieldState) = listSaver<TextFieldState, Any>(
    save = { listOf(it.text, it.isFocused, state.isFocusedDirty, state.displayError) },
    restore = {
        state.apply {
            text = it[0] as String
            isFocused = it[1] as Boolean
            isFocusedDirty = it[2] as Boolean
            displayError = it[3] as Boolean
        }
    }
)