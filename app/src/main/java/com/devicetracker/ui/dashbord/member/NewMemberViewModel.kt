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
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class NewMemberViewModel @Inject constructor(
    private val repo: MemberRepository
) :  ViewModel() {
    //private val _members = MutableLiveData<List<Member>>()
    private val _member = MutableLiveData<Member>()
    //var members: LiveData<List<Member>> = _members
    val member: LiveData<Member> = _member

    var addedMemberResponse by mutableStateOf<AddMemberResponse>(Response.Success(false))
        private set

    var isLoaderShowing by mutableStateOf<Boolean>(true)
        private set

    /*var getMemberResponse by mutableStateOf<GetMembersResponse>(Response.Success(null))
        private set
*/
    var getMembersByIdResponse by mutableStateOf<GetMembersByIdResponse>(Response.Success(null))
        private set

    fun addNewMember(employeeCode: Int, memberName: String, emailAddress: String, imageUrl: String, isMemberWritablePermission: Boolean) = viewModelScope.launch {
        addedMemberResponse = Response.Loading
        Log.d("MemberVM", "nkp employeeCode $employeeCode, memberName $memberName, emailAddress $emailAddress isMemberWritablePermission $isMemberWritablePermission")
        addedMemberResponse = repo.addMember(employeeCode,memberName, emailAddress, imageUrl, isMemberWritablePermission)
    }

    fun uploadImageAndAddNewMemberToFirebase(
        imageUri: Uri?,
        imageBitmap: Bitmap?,
        employeeCode: Int,
        memberName: String,
        emailAddress: String,
        isMemberWritablePermission: Boolean,
        onNavUp: () -> Unit
    ) = viewModelScope.launch {
        addedMemberResponse = Response.Loading
        addedMemberResponse = repo.uploadImageAndAddNewMemberToFirebase(imageUri, imageBitmap, employeeCode,memberName, emailAddress, isMemberWritablePermission, onNavUp)
    }

    /*var members = liveData(Dispatchers.IO) {
        isLoaderShowing = true
        val result = repo.getMembersFromFirebase()
        isLoaderShowing = false
        emit(result)
    }*/

    val members = liveData(Dispatchers.IO) {
        emit(fetchMembers())
    }

    private suspend fun fetchMembers(): GetMembersResponse {
        isLoaderShowing = true
        val result = repo.getMembersFromFirebase()
        isLoaderShowing = false
        return result
    }

    fun refreshMembers() = liveData(Dispatchers.IO) {
        emit(fetchMembers())
    }

    /*fun fetchMembers() = viewModelScope.launch {
        isLoaderShowing = true
        getMemberResponse = repo.getMembersFromFirebase()
        if(getMemberResponse is Response.Success) {
            try {
                val querySnapshot = (getMemberResponse as Response.Success<QuerySnapshot?>).data
                if (querySnapshot != null && !querySnapshot.isEmpty) {
                    val members = querySnapshot.documents.map { document ->
                        val member = document.toObject(Member::class.java) ?: Member()
                        member.memberId = document.id
                        member
                    }
                    isLoaderShowing = false
                    _members.value = members
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error data fetching : ${e.printStackTrace()}")
            }
        }
    }*/

    fun getMemberDetailById(memberId: String) = viewModelScope.launch {
        isLoaderShowing = true
        getMembersByIdResponse = repo.getMembersDetailById(memberId)
        if(getMembersByIdResponse is Response.Success){
            try {
                val documentSnapshot = (getMembersByIdResponse as Response.Success<DocumentSnapshot?>).data
                if (documentSnapshot != null) {
                    val member = documentSnapshot.toObject(Member::class.java) ?: Member()
                    member.memberId = documentSnapshot.id
                    isLoaderShowing = false
                    _member.value = member
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error data fetching : ${e.printStackTrace()}")
            }
        }
    }

    companion object {
        private const val TAG = "NewMemberViewModel"
    }
}