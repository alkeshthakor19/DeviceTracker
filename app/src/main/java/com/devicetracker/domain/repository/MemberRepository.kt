package com.devicetracker.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.dashbord.member.Member

typealias AddMemberResponse = Response<Boolean>
typealias GetMembersResponse = List<Member>
typealias GetMembersByIdResponse = Member?

interface MemberRepository {
    suspend fun addMember(employeeCode: Int, memberName: String, emailAddress: String, imageUrl: String, memberEditablePermission: Boolean, assetEditablePermission: Boolean, mobileNumber: String): AddMemberResponse

    suspend fun uploadImageAndAddNewMemberToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        memberEditablePermission: Boolean,
        assetEditablePermission: Boolean,
        mobileNumber: String,
        onNavUp: () -> Unit
    ) : AddMemberResponse

    suspend fun getMembersFromFirebase() : GetMembersResponse

    suspend fun getMembersDetailById(memberId: String) : GetMembersByIdResponse

    suspend fun isMemberEditablePermission() : Boolean
}