package com.devicetracker.ui.components

class MemberNameState : TextFieldState(validator = ::isMemberNameValid, errorFor = ::memberNameValidationError)

private fun isMemberNameValid(memberName: String): Boolean {
    return memberName.isNotEmpty()
}

private fun memberNameValidationError(memberName: String): String {
    return "Please enter member name."
}