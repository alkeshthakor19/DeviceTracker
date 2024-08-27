package com.devicetracker.ui.components

class PasswordState : TextFieldState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)
private fun isPasswordValid(password: String): Boolean {
    return password.length > 3
}

private fun passwordValidationError(password: String): String {
    return "Invalid password"
}