package com.devicetracker

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(@SerialName("Id") val id: String,
                @SerialName("Name") val name: String,
                @SerialName("Emp_Code") val empCode: Int,
                @SerialName("Email") val email: String,
                @SerialName("Created_At")val createdAt: String,
                @SerialName("Deleted_At")val deletedAt: String?)



