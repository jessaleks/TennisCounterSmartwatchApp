package com.aleksanderjess.tenniscounter.annotations

import androidx.compose.ui.tooling.preview.Preview

@Preview(
    device = androidx.wear.tooling.preview.devices.WearDevices.SMALL_ROUND,
    showSystemUi = true
)
annotation class SmallRoundWearPreview

@Preview(
    device = androidx.wear.tooling.preview.devices.WearDevices.SQUARE,
    showSystemUi = true,

    )
annotation class SquareWearPreview()
