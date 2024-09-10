package com.devicetracker.ui.components

class AssetSerialNumberState : TextFieldState(validator = ::isAssetSerialNumberValid, errorFor = ::assetSerialNumberValidationError)

private fun isAssetSerialNumberValid(assetSerialNumber: String): Boolean {
    return assetSerialNumber.isNotEmpty()
}

private fun assetSerialNumberValidationError(assetSerialNumber: String): String {
    return "Please enter asset serial number."
}