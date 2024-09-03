package com.devicetracker.ui.dashbord.assets

fun getModelsForAssetType(assetType: AssetType): List<String> {
    return when (assetType) {
        AssetType.TAB -> listOf(
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
        AssetType.CABLE -> listOf(
            "Type-C"
        )
        AssetType.USB -> listOf(
            "Trizon USB"
        )
        AssetType.PROBE -> listOf(
            "TORSO one",
            "TORSO",
            "Lexsa"
        )
        AssetType.OTHER -> listOf(
            "NA"
        )
    }
}
