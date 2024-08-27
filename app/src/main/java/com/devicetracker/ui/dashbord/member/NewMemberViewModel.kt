package com.devicetracker.ui.dashbord.member

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devicetracker.core.Utils.Companion.showMessage
import com.devicetracker.domain.models.Response
import com.devicetracker.domain.repository.AddMemberResponse
import com.devicetracker.domain.repository.GetMembersResponse
import com.devicetracker.domain.repository.MemberRepository
import com.devicetracker.domain.repository.UploadImageResponse
import com.devicetracker.ui.ProgressBar
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class NewMemberViewModel @Inject constructor(
    private val repo: MemberRepository
) :  ViewModel() {
    private val _members = MutableLiveData<List<Member>>()
    val members: LiveData<List<Member>> = _members

    var addedMemberResponse by mutableStateOf<AddMemberResponse>(Response.Success(false))
        private set

    var getMemberResponse by mutableStateOf<GetMembersResponse>(Response.Success(null))
        private set

    fun addNewMember(employeeCode: Int, memberName: String, emailAddress: String, imageUrl: String, isMemberWritablePermission: Boolean) = viewModelScope.launch {
        addedMemberResponse = Response.Loading
        Log.d("MemberVM", "nkp employeeCode $employeeCode, memberName $memberName, emailAddress $emailAddress isMemberWritablePermission $isMemberWritablePermission")
        addedMemberResponse = repo.addMember(employeeCode,memberName, emailAddress, imageUrl, isMemberWritablePermission)
    }

    fun uploadImageAndAddNewMemberToFirebase(imageUri: Uri?, imageBitmap: Bitmap?, employeeCode: Int, memberName: String, emailAddress: String, isMemberWritablePermission: Boolean) = viewModelScope.launch {
        addedMemberResponse = Response.Loading
        addedMemberResponse = repo.uploadImageAndAddNewMemberToFirebase(imageUri, imageBitmap, employeeCode,memberName, emailAddress, isMemberWritablePermission)
    }

    fun fetchMembers() = viewModelScope.launch {
        //getMemberResponse = Response.Loading
        getMemberResponse = repo.getMembersFromFirebase()
        if(getMemberResponse is Response.Success){
            val querySnapshot = (getMemberResponse as Response.Success<QuerySnapshot?>).data
            if (querySnapshot != null && !querySnapshot.isEmpty) {
                val members = querySnapshot.documents.map { document ->
                    document.toObject(Member::class.java) ?: Member()
                }
                _members.value = members
            }
        }
    }
}