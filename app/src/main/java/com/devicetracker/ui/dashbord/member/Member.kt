package com.devicetracker.ui.dashbord.member

import com.google.firebase.Timestamp

data class Member(
    val employeeCode: Int? = null,
    val memberName: String = "",
    val emailAddress: String = "",
    val imageUrl: String = "",
    val isWritablePermission: Boolean = false,
    val createdAt : Timestamp? = null
)
