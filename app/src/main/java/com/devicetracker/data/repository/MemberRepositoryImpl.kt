package com.devicetracker.data.repository

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.devicetracker.core.Constants.ASSET_DOC_ID
import com.devicetracker.core.Constants.ASSET_EDITABLE_PERMISSION
import com.devicetracker.core.Constants.COLLECTION_ASSETS
import com.devicetracker.core.Constants.COLLECTION_ASSETS_HISTORY
import com.devicetracker.core.Constants.COLLECTION_MEMBERS
import com.devicetracker.core.Constants.CREATED_AT
import com.devicetracker.core.Constants.EMAIL_ADDRESS
import com.devicetracker.core.Constants.EMPLOYEE_CODE
import com.devicetracker.core.Constants.FIRE_STORAGE_IMAGES
import com.devicetracker.core.Constants.IMAGE_URL
import com.devicetracker.core.Constants.MEMBER_EDITABLE_PERMISSION
import com.devicetracker.core.Constants.MEMBER_NAME
import com.devicetracker.core.Constants.MOBILE_NUMBER
import com.devicetracker.domain.models.Response.Failure
import com.devicetracker.domain.models.Response.Success
import com.devicetracker.domain.repository.AddMemberResponse
import com.devicetracker.domain.repository.GetMembersByIdResponse
import com.devicetracker.domain.repository.GetMembersResponse
import com.devicetracker.domain.repository.MemberRepository
import com.devicetracker.domain.repository.UpdateMemberResponse
import com.devicetracker.ui.dashbord.member.Member
import com.google.firebase.auth.FirebaseAuth
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
        memberEditablePermission: Boolean,
        assetEditablePermission: Boolean,
        mobileNumber: String
    ): AddMemberResponse = try {
        val member = hashMapOf(
            EMPLOYEE_CODE to employeeCode,
            MEMBER_NAME to memberName,
            EMAIL_ADDRESS to emailAddress,
            IMAGE_URL to imageUrl,
            MEMBER_EDITABLE_PERMISSION to memberEditablePermission,
            ASSET_EDITABLE_PERMISSION to assetEditablePermission,
            MOBILE_NUMBER to mobileNumber,
            CREATED_AT to serverTimestamp()
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
        memberEditablePermission: Boolean,
        assetEditablePermission: Boolean,
        mobileNumber: String,
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
        addMember(employeeCode, memberName, emailAddress, resultUri.toString(), memberEditablePermission, assetEditablePermission, mobileNumber)
        onNavUp()
        Success(true)
    } catch (e: Exception) {
        e.printStackTrace()
        Failure(e)
    }

    override suspend fun getMembersFromFirebase() : GetMembersResponse {
        val querySnapshot = db.collection(COLLECTION_MEMBERS).get().await()
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
        var member  = Member()
        try {
            if(document.toObject(Member::class.java) != null){
                member = document.toObject(Member::class.java)!!
                member.memberId = document.id
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return member
    }

    override suspend fun isMemberEditablePermission(): Boolean {
        val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email
        var isMemberEditableUser = false
        val querySnapshot = db.collection(COLLECTION_MEMBERS).whereEqualTo(EMAIL_ADDRESS, currentUserEmail).get().await()
        try {
            for (document in querySnapshot.documents) {
                val member = document.toObject(Member::class.java)
                if(member != null) {
                    isMemberEditableUser = member.memberEditablePermission
                }
            }
        } catch (e: Exception){
            e.printStackTrace()
        }
        return isMemberEditableUser
    }

    override suspend fun deleteMember(memberId: String, onSuccess: () -> Unit) {
        db.collection(COLLECTION_MEMBERS).document(memberId).delete()
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                // Handle failure
                Log.w("MemberRepositoryImp", "nkp Error deleting Member", e)
            }
    }

    override suspend fun updateMember(
        memberId: String,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        memberEditablePermission: Boolean,
        assetEditablePermission: Boolean,
        mobileNumber: String,
        imageUrl: String?
    ): UpdateMemberResponse {

        return try {
            val memberData = hashMapOf(
                EMPLOYEE_CODE to employeeCode,
                MEMBER_NAME to memberName,
                EMAIL_ADDRESS to emailAddress,
                MEMBER_EDITABLE_PERMISSION to memberEditablePermission,
                ASSET_EDITABLE_PERMISSION to assetEditablePermission,
                MOBILE_NUMBER to mobileNumber,
                CREATED_AT to serverTimestamp()
            )
            if (imageUrl != null) {
                memberData[IMAGE_URL] = imageUrl
            }
            db.collection(COLLECTION_MEMBERS).document(memberId).update(memberData).await()
            Success(true)
        } catch (e: Exception) {
            e.printStackTrace()
            Failure(e)
        }
    }
    override suspend fun uploadImageAndUpdateMember(
        memberId: String,
        isNeedToUpdateImageUrl: Boolean,
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        memberEditablePermission: Boolean,
        assetEditablePermission: Boolean,
        mobileNumber: String,
        onNavUp: () -> Unit
    ): UpdateMemberResponse {
        return try {
            Log.d("check","isNeedToUpdateImageUrl: $isNeedToUpdateImageUrl")
            val resultUri = if(isNeedToUpdateImageUrl){
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
                resultUri.toString()
            } else {
                null
            }

            // After uploading the image, update the asset
            val updateResponse = updateMember(
                memberId,
                employeeCode,
                memberName,
                emailAddress,
                memberEditablePermission,
                assetEditablePermission,
                mobileNumber,
                resultUri,
            )
            if (updateResponse is Success && updateResponse.data) {
                onNavUp()
            }
            updateResponse
        } catch (e: Exception) {
            e.printStackTrace()
            Failure(e)
        }
    }
}