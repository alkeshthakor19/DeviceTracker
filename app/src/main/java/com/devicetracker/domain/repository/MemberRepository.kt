package com.devicetracker.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.devicetracker.domain.models.Response
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot

typealias AddMemberResponse = Response<Boolean>
typealias UploadImageResponse = Response<Uri?>
//typealias GetMembersResponse = Response<QuerySnapshot?>
typealias GetMembersResponse = List<Member>
typealias GetMembersByIdResponse = Response<DocumentSnapshot?>

interface MemberRepository {
    suspend fun addMember(employeeCode: Int, memberName: String, emailAddress: String, imageUrl: String, isWritablePermission: Boolean): AddMemberResponse

    suspend fun uploadImageAndAddNewMemberToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        isWritablePermission: Boolean,
        onNavUp: () -> Unit
    ) : AddMemberResponse

    suspend fun getMembersFromFirebase() : GetMembersResponse

    suspend fun getMembersDetailById(memberId: String) : GetMembersByIdResponse
}