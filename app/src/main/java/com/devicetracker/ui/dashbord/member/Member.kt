package com.devicetracker.ui.dashbord.member

import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
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

val MemberSaver: Saver<Member, Any> = listSaver(
    save = {
        listOf(
            it.memberId,
            it.employeeCode,
            it.memberName,
            it.emailAddress,
            it.mobileNumber,
            it.imageUrl,
            it.memberEditablePermission,
            it.assetEditablePermission,
            it.createdAt?.seconds ?: 0L, // Use 0L or other default value if null
            it.createdAt?.nanoseconds ?: 0 // Use 0 or other default value if null
        )
    },
    restore = {
        val seconds = it[8] as Long
        val nanoseconds = it[9] as Int
        Member(
            memberId = it[0] as String?,
            employeeCode = it[1] as Int?,
            memberName = it[2] as String,
            emailAddress = it[3] as String,
            mobileNumber = it[4] as String,
            imageUrl = it[5] as String,
            memberEditablePermission = it[6] as Boolean,
            assetEditablePermission = it[7] as Boolean,
            createdAt = if (seconds != 0L || nanoseconds != 0)
                Timestamp(seconds, nanoseconds)
            else null
        )
    }
)
