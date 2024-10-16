package com.devicetracker.ui.components

class AssetQuantityState : TextFieldState(validator = ::isAssetQuantityValid, errorFor = ::assetQuantityValidationError)

private fun isAssetQuantityValid(assetQuantity: String): Boolean {
    return assetQuantity.isNotEmpty()
}

private fun assetQuantityValidationError(assetQuantity: String): String {
    return "Please enter asset quantity."
}

val AssetQuantityStateSaver = textFiledStateSaver(AssetQuantityState())