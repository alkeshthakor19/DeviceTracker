package com.devicetracker.ui.components

class AssetDescriptionState: TextFieldState(validator = ::isAssetDescriptionValid, errorFor = ::assetDescriptionValidationError)

private fun isAssetDescriptionValid(assetDescription: String): Boolean {
    return assetDescription.isNotEmpty()
}

private fun assetDescriptionValidationError(assetDescription: String): String {
    return "Please enter description."
}

val AssetDescriptionStateSaver = textFiledStateSaver(AssetDescriptionState())