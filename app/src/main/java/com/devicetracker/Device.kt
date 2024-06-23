package com.devicetracker

import kotlinx.serialization.Serializable

@Serializable
data class Device(
    val id: Int,
    val name: String,
    val type: Int)

enum class DeviceType {
    TAB,
    USB,
    CABLE,
    PROB,
    OTHER
}
