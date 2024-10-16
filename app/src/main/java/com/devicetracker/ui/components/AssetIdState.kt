package com.devicetracker.ui.components

class AssetIdState : TextFieldState(validator = ::isAssetIdValid, errorFor = ::assetIdValidationError)

private fun isAssetIdValid(assetId: String): Boolean {
    return assetId.isNotEmpty()
}

private fun assetIdValidationError(assetId: String): String {
    return "Please enter asset id."
}

val AssetIdStateSaver = textFiledStateSaver(AssetIdState())