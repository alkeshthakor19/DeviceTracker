package com.devicetracker.ui.components

import com.devicetracker.core.Constants

class MobileNumberState : TextFieldState(validator = ::isMobileNumberValid, errorFor = ::mobileNumberValidationError)
private fun isMobileNumberValid(mobileNumber: String): Boolean {
    return mobileNumber.isNotEmpty()
}

private fun mobileNumberValidationError(mobileNumber: String): String {
    return if(mobileNumber.isEmpty()) {
        "Please enter mobile number."
    } else {
        Constants.EMPTY_STR
    }
}

val MobileNumberStateSaver = textFiledStateSaver(MobileNumberState())