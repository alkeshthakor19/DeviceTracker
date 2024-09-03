package com.devicetracker.ui.components

class AssetNameState : TextFieldState(validator = ::isAssetNameValid, errorFor = ::assetNameValidationError)

private fun isAssetNameValid(assetName: String): Boolean {
    return assetName.isNotEmpty()
}

private fun assetNameValidationError(assetName: String): String {
    return "Please enter asset name."
}