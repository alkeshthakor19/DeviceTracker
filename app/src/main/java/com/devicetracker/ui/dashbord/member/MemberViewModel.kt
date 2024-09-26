package com.devicetracker.ui.dashbord.member

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.devicetracker.domain.models.Response
import com.devicetracker.domain.repository.AddMemberResponse
import com.devicetracker.domain.repository.GetMembersByIdResponse
import com.devicetracker.domain.repository.GetMembersResponse
import com.devicetracker.domain.repository.MemberRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MemberViewModel @Inject constructor(
    private val repo: MemberRepository
) :  ViewModel() {
    private val _members = MutableLiveData<List<Member>>()
    private val _member = MutableLiveData<Member>()
    var members: LiveData<List<Member>> = _members
    val member: LiveData<Member> get() = _member

    var addedMemberResponse by mutableStateOf<AddMemberResponse>(Response.Success(false))
        private set

    var isLoaderShowing by mutableStateOf(true)
        private set

    init {
        refreshMembers()
    }

    fun addNewMember(employeeCode: Int, memberName: String, emailAddress: String, imageUrl: String, memberEditablePermission: Boolean, assetEditablePermission: Boolean, mobileNumber: String) = viewModelScope.launch {
        addedMemberResponse = Response.Loading
        Log.d("MemberVM", "nkp employeeCode $employeeCode, memberName $memberName, emailAddress $emailAddress memberEditablePermission $memberEditablePermission")
        addedMemberResponse = repo.addMember(employeeCode,memberName, emailAddress, imageUrl, memberEditablePermission, assetEditablePermission, mobileNumber)
    }

    fun uploadImageAndAddNewMemberToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        memberEditablePermission: Boolean,
        assetEditablePermission: Boolean,
        mobileNumber: String,
        onNavUp: () -> Unit
    ) = viewModelScope.launch {
        addedMemberResponse = Response.Loading
        addedMemberResponse = repo.uploadImageAndAddNewMemberToFirebase(imageUri, imageBitmap, employeeCode,memberName, emailAddress, memberEditablePermission, assetEditablePermission, mobileNumber, onNavUp)
    }

    fun refreshMembers() {
        viewModelScope.launch(Dispatchers.IO) {
            val newMember = fetchMembers()
            _members.postValue(newMember)
        }
    }

    suspend fun fetchMembers(): GetMembersResponse {
        Log.d("MemberViewModel", "nkp call fetchMembers()")
        isLoaderShowing = true
        val result = repo.getMembersFromFirebase()
        isLoaderShowing = false
        return result
    }

    fun fetchMember(memberId: String) = liveData(Dispatchers.IO) {
        emit(getMemberDetailById(memberId))
    }

    private suspend fun getMemberDetailById(memberId: String) : GetMembersByIdResponse {
        isLoaderShowing = true
        val result = repo.getMembersDetailById(memberId)
        isLoaderShowing = false
        return result
    }

    fun isMemberEditablePermission() = liveData(Dispatchers.IO) {
        emit(getMemberEditablePermission())
    }

    private suspend fun getMemberEditablePermission(): Boolean {
        val result = repo.isMemberEditablePermission()
        return result
    }

    companion object {
        private const val TAG = "NewMemberViewModel"
    }
}