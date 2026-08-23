package com.android.purebilibili.feature.video.ui.overlay

import com.android.purebilibili.core.util.VrPanelRuntimeFlags

data class BottomRightControlsLayoutPolicy(
    val rowSpacingDp: Int,
    val menuOffsetYDp: Int,
    val chipCornerRadiusDp: Int,
    val chipFontSp: Int,
    val chipHorizontalPaddingDp: Int,
    val chipVerticalPaddingDp: Int
)

fun resolveBottomRightControlsLayoutPolicy(
    widthDp: Int
): BottomRightControlsLayoutPolicy {
    // VR 面板：倍速/画质/比例大 chip（docs/vr/gesture-comfort.md §2.1）
    if (VrPanelRuntimeFlags.activityEmbeddedInVrPanel) {
        return BottomRightControlsLayoutPolicy(
            rowSpacingDp = 16,
            menuOffsetYDp = -14,
            chipCornerRadiusDp = 12,
            chipFontSp = 20,
            chipHorizontalPaddingDp = 18,
            chipVerticalPaddingDp = 12
        )
    }

    if (widthDp >= 1600) {
        return BottomRightControlsLayoutPolicy(
            rowSpacingDp = 12,
            menuOffsetYDp = -12,
            chipCornerRadiusDp = 8,
            chipFontSp = 14,
            chipHorizontalPaddingDp = 12,
            chipVerticalPaddingDp = 7
        )
    }

    if (widthDp >= 840) {
        return BottomRightControlsLayoutPolicy(
            rowSpacingDp = 10,
            menuOffsetYDp = -11,
            chipCornerRadiusDp = 7,
            chipFontSp = 13,
            chipHorizontalPaddingDp = 11,
            chipVerticalPaddingDp = 6
        )
    }

    if (widthDp >= 600) {
        return BottomRightControlsLayoutPolicy(
            rowSpacingDp = 9,
            menuOffsetYDp = -10,
            chipCornerRadiusDp = 6,
            chipFontSp = 12,
            chipHorizontalPaddingDp = 10,
            chipVerticalPaddingDp = 6
        )
    }

    return BottomRightControlsLayoutPolicy(
        rowSpacingDp = 8,
        menuOffsetYDp = -10,
        chipCornerRadiusDp = 6,
        chipFontSp = 12,
        chipHorizontalPaddingDp = 10,
        chipVerticalPaddingDp = 6
    )
}
