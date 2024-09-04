package com.devicetracker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.devicetracker.core.Constants.COLLECTION_MEMBERS
import com.devicetracker.core.Constants.FIRE_STORAGE_IMAGES
import com.devicetracker.domain.models.Response.Failure
import com.devicetracker.domain.models.Response.Success
import com.devicetracker.domain.repository.AddMemberResponse
import com.devicetracker.domain.repository.GetMembersByIdResponse
import com.devicetracker.domain.repository.GetMembersResponse
import com.devicetracker.domain.repository.MemberRepository
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.firestore.FieldValue.serverTimestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MemberRepositoryImpl @Inject constructor(private val db: FirebaseFirestore, private val storageReference: StorageReference) : MemberRepository {
    override suspend fun addMember(
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        imageUrl: String,
        isWritablePermission: Boolean
    ): AddMemberResponse = try {
        val member = hashMapOf(
            "employeeCode" to employeeCode,
            "memberName" to memberName,
            "emailAddress" to emailAddress,
            "imageUrl" to imageUrl,
            "isWritablePermission" to isWritablePermission,
            "createdAt" to serverTimestamp()
        )

        db.collection(COLLECTION_MEMBERS).add(member).await()
        Success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }

    override suspend fun uploadImageAndAddNewMemberToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        isWritablePermission: Boolean,
        onNavUp: () -> Unit
    ): AddMemberResponse = try {
        val imageRef = storageReference.child("$FIRE_STORAGE_IMAGES/${UUID.randomUUID()}.jpg")
        val uploadTask = if (imageUri != null) {
            imageRef.putFile(imageUri)
        } else {
            val data = imageBitmap?.let {
                ByteArrayOutputStream().apply {
                    it.compress(Bitmap.CompressFormat.JPEG, 100, this)
                }.toByteArray()
            }
            imageRef.putBytes(data ?: byteArrayOf())
        }

        uploadTask.await()
        val resultUri = imageRef.downloadUrl.await()
        addMember(employeeCode, memberName, emailAddress, resultUri.toString(), isWritablePermission)
        onNavUp()
        Log.d("MemberRepositoryImpl", "nkp uploadImageAndAddNewMemberToFirebase()")
        Success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }

    override suspend fun getMembersFromFirebase() : GetMembersResponse {
        val querySnapshot = db.collection(COLLECTION_MEMBERS).get().await()
        Log.d("MemberRepositoryImpl", "nkp getMembersFromFirebase()")
        var members : List<Member> = emptyList()
        try {
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                members = querySnapshot.documents.map { document ->
                    val member = document.toObject(Member::class.java) ?: Member()
                    member.memberId = document.id
                    member
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

        return members
    }

    override suspend fun getMembersDetailById(memberId: String): GetMembersByIdResponse {
        val document = db.collection(COLLECTION_MEMBERS).document(memberId).get().await()
        var member : Member? = null
        try {
            member = document.toObject(Member::class.java)
            if (member != null) {
                member.memberId = document.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.d("MemberRepositoryImpl", "nkp getMembersDetailById()")
        return member
    }
}