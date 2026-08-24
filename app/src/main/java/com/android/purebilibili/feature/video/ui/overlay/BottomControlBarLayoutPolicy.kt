package com.android.purebilibili.feature.video.ui.overlay

import com.android.purebilibili.core.store.PlayerProgressPlacement
import com.android.purebilibili.core.util.VrPanelRuntimeFlags

data class BottomControlBarLayoutPolicy(
    val bottomPaddingDp: Int,
    val progressSpacingDp: Int,
    val horizontalPaddingDp: Int,
    val playButtonSizeDp: Int,
    val playIconSizeDp: Int,
    val afterPlaySpacingDp: Int,
    val timeFontSp: Int,
    val afterTimeSpacingDp: Int,
    val danmakuIconSizeDp: Int,
    val danmakuSwitchToInputSpacingDp: Int,
    val danmakuSwitchHorizontalPaddingDp: Int,
    val danmakuSwitchVerticalPaddingDp: Int,
    val danmakuInputHeightDp: Int,
    val danmakuInputStartPaddingDp: Int,
    val danmakuInputFontSp: Int,
    val danmakuSettingButtonSizeDp: Int,
    val danmakuSettingEndPaddingDp: Int,
    val danmakuSettingIconSizeDp: Int,
    val afterInputSpacingDp: Int,
    val rightActionSpacingDp: Int,
    val actionChipHorizontalPaddingDp: Int,
    val actionChipVerticalPaddingDp: Int,
    val actionTextFontSp: Int,
    val fullscreenIconSizeDp: Int
)

internal fun resolveBottomControlBarBottomPaddingDp(
    defaultBottomPaddingDp: Int,
    progressPlacement: PlayerProgressPlacement
): Int {
    return if (progressPlacement == PlayerProgressPlacement.BOTTOM_EDGE) {
        0
    } else {
        defaultBottomPaddingDp
    }
}

/**
 * Keeps the inline detail player's scrubber anchored to the video edge.
 *
 * The phone detail player is always drawn edge-to-edge, including when the transparent system
 * status bar remains visible. Placing the progress bar above the controls makes it appear in
 * the middle of the video frame.
 */
internal fun resolveVideoDetailProgressPlacement(
    requestedPlacement: PlayerProgressPlacement,
    isFullscreen: Boolean
): PlayerProgressPlacement {
    return if (!isFullscreen) {
        PlayerProgressPlacement.BOTTOM_EDGE
    } else {
        requestedPlacement
    }
}

fun resolveBottomControlBarLayoutPolicy(
    widthDp: Int
): BottomControlBarLayoutPolicy {
    // VR 面板：射线瞄准需要大控件（docs/vr/gesture-comfort.md §2.1），不受窗口宽度分桶影响
    if (VrPanelRuntimeFlags.activityEmbeddedInVrPanel) {
        return BottomControlBarLayoutPolicy(
            bottomPaddingDp = 20,
            progressSpacingDp = 14,
            horizontalPaddingDp = 28,
            playButtonSizeDp = 60,
            playIconSizeDp = 44,
            afterPlaySpacingDp = 14,
            timeFontSp = 17,
            afterTimeSpacingDp = 16,
            danmakuIconSizeDp = 36,
            danmakuSwitchToInputSpacingDp = 12,
            danmakuSwitchHorizontalPaddingDp = 12,
            danmakuSwitchVerticalPaddingDp = 9,
            danmakuInputHeightDp = 52,
            danmakuInputStartPaddingDp = 18,
            danmakuInputFontSp = 16,
            danmakuSettingButtonSizeDp = 46,
            danmakuSettingEndPaddingDp = 6,
            danmakuSettingIconSizeDp = 26,
            afterInputSpacingDp = 14,
            rightActionSpacingDp = 14,
            actionChipHorizontalPaddingDp = 10,
            actionChipVerticalPaddingDp = 7,
            actionTextFontSp = 17,
            fullscreenIconSizeDp = 30
        )
    }

    if (widthDp >= 1600) {
        return BottomControlBarLayoutPolicy(
            bottomPaddingDp = 18,
            progressSpacingDp = 12,
            horizontalPaddingDp = 32,
            playButtonSizeDp = 48,
            playIconSizeDp = 36,
            afterPlaySpacingDp = 12,
            timeFontSp = 15,
            afterTimeSpacingDp = 20,
            danmakuIconSizeDp = 32,
            danmakuSwitchToInputSpacingDp = 12,
            danmakuSwitchHorizontalPaddingDp = 10,
            danmakuSwitchVerticalPaddingDp = 7,
            danmakuInputHeightDp = 52,
            danmakuInputStartPaddingDp = 18,
            danmakuInputFontSp = 16,
            danmakuSettingButtonSizeDp = 44,
            danmakuSettingEndPaddingDp = 6,
            danmakuSettingIconSizeDp = 24,
            afterInputSpacingDp = 16,
            rightActionSpacingDp = 16,
            actionChipHorizontalPaddingDp = 8,
            actionChipVerticalPaddingDp = 5,
            actionTextFontSp = 17,
            fullscreenIconSizeDp = 28
        )
    }

    if (widthDp >= 840) {
        return BottomControlBarLayoutPolicy(
            bottomPaddingDp = 14,
            progressSpacingDp = 10,
            horizontalPaddingDp = 24,
            playButtonSizeDp = 40,
            playIconSizeDp = 32,
            afterPlaySpacingDp = 10,
            timeFontSp = 13,
            afterTimeSpacingDp = 16,
            danmakuIconSizeDp = 28,
            danmakuSwitchToInputSpacingDp = 10,
            danmakuSwitchHorizontalPaddingDp = 8,
            danmakuSwitchVerticalPaddingDp = 6,
            danmakuInputHeightDp = 44,
            danmakuInputStartPaddingDp = 16,
            danmakuInputFontSp = 14,
            danmakuSettingButtonSizeDp = 36,
            danmakuSettingEndPaddingDp = 5,
            danmakuSettingIconSizeDp = 20,
            afterInputSpacingDp = 12,
            rightActionSpacingDp = 14,
            actionChipHorizontalPaddingDp = 7,
            actionChipVerticalPaddingDp = 4,
            actionTextFontSp = 15,
            fullscreenIconSizeDp = 24
        )
    }

    if (widthDp >= 600) {
        return BottomControlBarLayoutPolicy(
            bottomPaddingDp = 13,
            progressSpacingDp = 9,
            horizontalPaddingDp = 20,
            playButtonSizeDp = 36,
            playIconSizeDp = 30,
            afterPlaySpacingDp = 9,
            timeFontSp = 13,
            afterTimeSpacingDp = 14,
            danmakuIconSizeDp = 26,
            danmakuSwitchToInputSpacingDp = 10,
            danmakuSwitchHorizontalPaddingDp = 8,
            danmakuSwitchVerticalPaddingDp = 6,
            danmakuInputHeightDp = 40,
            danmakuInputStartPaddingDp = 15,
            danmakuInputFontSp = 14,
            danmakuSettingButtonSizeDp = 34,
            danmakuSettingEndPaddingDp = 4,
            danmakuSettingIconSizeDp = 19,
            afterInputSpacingDp = 12,
            rightActionSpacingDp = 14,
            actionChipHorizontalPaddingDp = 7,
            actionChipVerticalPaddingDp = 4,
            actionTextFontSp = 14,
            fullscreenIconSizeDp = 22
        )
    }

    return BottomControlBarLayoutPolicy(
        bottomPaddingDp = 12,
        progressSpacingDp = 8,
        horizontalPaddingDp = 12,
        playButtonSizeDp = 32,
        playIconSizeDp = 28,
        afterPlaySpacingDp = 8,
        timeFontSp = 12,
        afterTimeSpacingDp = 8,
        danmakuIconSizeDp = 24,
        danmakuSwitchToInputSpacingDp = 8,
        danmakuSwitchHorizontalPaddingDp = 6,
        danmakuSwitchVerticalPaddingDp = 6,
        danmakuInputHeightDp = 36,
        danmakuInputStartPaddingDp = 14,
        danmakuInputFontSp = 13,
        danmakuSettingButtonSizeDp = 32,
        danmakuSettingEndPaddingDp = 4,
        danmakuSettingIconSizeDp = 18,
        afterInputSpacingDp = 10,
        rightActionSpacingDp = 8,
        actionChipHorizontalPaddingDp = 5,
        actionChipVerticalPaddingDp = 4,
        actionTextFontSp = 13,
        fullscreenIconSizeDp = 20
    )
}
