package com.devicetracker

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue

open class TextFiledState(
    private val validator: (String) -> Boolean = {true},
    private val errorFor:(String) -> String = {""}
) {

    var text: String by mutableStateOf("")

    var isFocused: Boolean by mutableStateOf(false)

    var isFocusedDirty: Boolean by mutableStateOf(false)

    private var displayError: Boolean by mutableStateOf(false)


    open val isValid: Boolean
        get() = validator(text)


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
            errorFor(text)
        } else null
    }
}

fun textFiledStateSaver(state: TextFiledState) = listSaver<TextFiledState, Any>(
    save = { listOf(it.text, it.isFocusedDirty) },
    restore = {
        state.apply {
            text = it[0] as String
            isFocusedDirty = it[1] as Boolean
        }
    }
)