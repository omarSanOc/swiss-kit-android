package com.epic_engine.swisskit.feature.qrscanner.domain.model

sealed interface CameraState {
    data object Checking : CameraState
    data object Authorized : CameraState
    data object Denied : CameraState
    data object Unavailable : CameraState
}

enum class ScanMode {
    SINGLE,
    CONTINUOUS
}
