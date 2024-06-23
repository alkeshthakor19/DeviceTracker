package com.devicetracker

import kotlinx.serialization.Serializable

@Serializable
data class Device(val id: Int, val name: String)
