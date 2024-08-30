package com.devicetracker.ui.components

class EmployeeIdState : TextFieldState(validator = ::isEmployeeIdValid, errorFor = ::employeeIdValidationError)
private fun isEmployeeIdValid(employeeCode: String): Boolean {
    return employeeCode.isNotEmpty() && (employeeCode.length == 6)
}

private fun employeeIdValidationError(employeeCode: String): String {
    return if(employeeCode.isEmpty()) {
        "Please enter employee id."
    } else if(employeeCode.length != 6){
        "Please enter 6 digit employee code."
    } else {
        ""
    }
}