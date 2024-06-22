package com.devicetracker

class PasswordState : TextFiledState(validator = ::isPasswordValid, errorFor = ::passwordValidationError)
private fun isPasswordValid(password: String): Boolean {
    return password.length > 3
}

private fun passwordValidationError(password: String): String {
    return "Invalid password"
}