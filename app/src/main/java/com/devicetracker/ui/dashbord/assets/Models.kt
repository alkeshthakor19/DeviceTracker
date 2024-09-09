package com.devicetracker.ui.dashbord.assets

fun getModelsForAssetType(assetType: String): List<String> {
    return when (assetType) {
        AssetType.TAB.name -> listOf(
            "Samsung Galaxy S6",
            "Samsung Galaxy Active TAB3",
            "Samsung Galaxy S8",
            "Samsung Galaxy S8 plus",
            "Samsung Galaxy S8 ultra",
            "Samsung Galaxy S9",
            "Lenovo 11",
            "Lenovo 11 Pro",
            "Lenovo 12",
            "Lenovo Yoga 13"
        )
        AssetType.CABLE.name -> listOf(
            "Type-C"
        )
        AssetType.USB.name -> listOf(
            "Trizon USB"
        )
        AssetType.PROBE.name -> listOf(
            "TORSO one",
            "TORSO",
            "Lexsa"
        )
        AssetType.OTHER.name -> emptyList()
        else -> {emptyList()}
    }
}
