package com.devicetracker.domain.repository

import android.graphics.Bitmap
import android.net.Uri
import com.devicetracker.domain.models.Response
import com.google.firebase.firestore.QuerySnapshot

typealias AddMemberResponse = Response<Boolean>
typealias UploadImageResponse = Response<Uri?>
typealias GetMembersResponse = Response<QuerySnapshot?>

interface MemberRepository {
    suspend fun addMember(employeeCode: Int, memberName: String, emailAddress: String, imageUrl: String, isWritablePermission: Boolean): AddMemberResponse

    suspend fun uploadImageAndAddNewMemberToFirebase(imageUri: Uri?, imageBitmap: Bitmap?, employeeCode: Int, memberName: String, emailAddress: String, isWritablePermission: Boolean) : AddMemberResponse

    suspend fun getMembersFromFirebase() : GetMembersResponse
}