package com.devicetracker.ui.dashbord.member

import com.devicetracker.core.Constants
import com.google.firebase.Timestamp

data class Member(
    var memberId: String? = null,
    val employeeCode: Int? = null,
    val memberName: String = Constants.EMPTY_STR,
    val emailAddress: String = Constants.EMPTY_STR,
    val mobileNumber: String = Constants.EMPTY_STR,
    val imageUrl: String = Constants.EMPTY_STR,
    val memberEditablePermission: Boolean = false,
    val assetEditablePermission: Boolean = false,
    val createdAt : Timestamp? = null
)
