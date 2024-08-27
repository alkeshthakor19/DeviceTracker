package com.devicetracker.ui.components

class EmployeeIdState : TextFieldState(validator = ::isEmployeeIdValid, errorFor = ::employeeIdValidationError)
private fun isEmployeeIdValid(employeeId: String): Boolean {
    return employeeId.isNotEmpty()
}

private fun employeeIdValidationError(employeeId: String): String {
    return "Please enter employee id."
}