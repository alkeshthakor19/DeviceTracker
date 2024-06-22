package com.devicetracker

import java.util.regex.Pattern

private const val EMAIL_VALIDATION_REGEX = "^(.+)@(.+)\$"

class EmailState (val email: String? = null) : TextFiledState(validator = :: isEmailValid, errorFor = ::emailValidationError) {
    init {
        email?.let {
            text = it
        }
    }
}

private fun emailValidationError(email: String): String {
    return "Invalid email: ${email}"
}

private fun isEmailValid(email: String): Boolean {
    return Pattern.matches(EMAIL_VALIDATION_REGEX, email)
}

val EmailStateSaver = textFiledStateSaver(EmailState())

