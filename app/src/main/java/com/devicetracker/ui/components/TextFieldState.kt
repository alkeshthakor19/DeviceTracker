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

    private var displayError: Boolean by mutableStateOf(false)


    open val isValid: Boolean
        get() = text.isNotBlank() && validator(text)


    fun onFocusChange(focused: Boolean) {
        isFocused = focused
    }

    fun enableShowError() {
            displayError = true
    }


    fun showError() = !isValid && displayError

    open fun getError(): String? {
        return if(showError()) {
            if (text.isBlank()) "Cannot be empty" else errorFor(text)
        } else null
    }
}

fun textFiledStateSaver(state: TextFieldState) = listSaver<TextFieldState, Any>(
    save = { listOf(it.text, it.isFocused) },
    restore = {
        state.apply {
            text = it[0] as String
            isFocused = it[1] as Boolean
        }
    }
)