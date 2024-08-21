package com.devicetracker.ui.home

import android.util.Patterns

fun isValidEmployeeId(employeeId: String): Boolean {
    return employeeId.toIntOrNull() != null
}

fun isValidUserName(userName: String): Boolean {
    return userName.all { it.isLetter() }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches() && email.endsWith("@einfochips.com")
}
