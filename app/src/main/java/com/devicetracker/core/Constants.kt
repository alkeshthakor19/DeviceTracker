package com.devicetracker.core

object Constants {
    //App
    const val TAG = "AppTag"

    const val EMPTY_STR = ""

    //Database
    const val COLLECTION_MEMBERS = "members"
    const val COLLECTION_ASSETS = "assets"
    const val COLLECTION_ASSETS_MODELS = "assetModels"
    const val COLLECTION_ASSETS_HISTORY = "assetsHistory"
    const val COLLECTION_PROJECTS = "projects"
    const val COLLECTION_ASSET_OWNER_HISTORY = "assetsOwnerHistory"
    const val FIRE_STORAGE_IMAGES = "images"

    // Asset field name
    const val ASSET_DOC_ID = "assetDocId"
    const val ASSET_ID = "assetId"
    const val ASSET_NAME = "assetName"
    const val ASSET_TYPE = "assetType"
    const val ASSET_MODEL_NAME = "modelName"
    const val ASSET_SERIAL_NUMBER = "serialNumber"
    const val ASSET_QUANTITY = "quantity"
    const val PROJECT_NAME = "projectName"
    const val ASSET_DESCRIPTION = "description"
    const val ASSET_OWNER_ID = "assetOwnerId"
    const val ASSET_OWNER_NAME = "assetOwnerName"
    const val LAST_VERIFICATION_AT = "lastVerificationAt"
    const val ASSET_WORKING_STATUS = "assetWorkingStatus"

    //Member
    const val EMPLOYEE_CODE = "employeeCode"
    const val MEMBER_NAME = "memberName"
    const val EMAIL_ADDRESS = "emailAddress"
    const val MEMBER_EDITABLE_PERMISSION = "memberEditablePermission"
    const val ASSET_EDITABLE_PERMISSION = "assetEditablePermission"
    const val MOBILE_NUMBER = "mobileNumber"

    // Common
    const val IMAGE_URL = "imageUrl"
    const val CREATED_AT = "createdAt"
    const val UPDATED_AT = "updatedAt"
    const val ADMIN_EMAIL = "adminEmail"

    const val UNASSIGN_NAME = "Un Assign"
    const val UNASSIGN_ID = "unassign"

    const val INT_SIZE_20 = 20
    const val INT_SIZE_24 = 24
    const val INT_SIZE_72 = 72
    const val INT_SIZE_80 = 80
}