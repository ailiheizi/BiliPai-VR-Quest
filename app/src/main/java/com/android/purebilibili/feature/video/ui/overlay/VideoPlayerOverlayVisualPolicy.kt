package com.android.purebilibili.feature.video.ui.overlay

import com.android.purebilibili.core.util.VrPanelRuntimeFlags

data class VideoPlayerOverlayVisualPolicy(
    val topScrimHeightDp: Int,
    val bottomScrimHeightDp: Int,
    val lockButtonEndPaddingDp: Int,
    val lockButtonSizeDp: Int,
    val lockButtonCornerRadiusDp: Int,
    val lockIconSizeDp: Int,
    val statsTopPaddingDp: Int,
    val statsEndPaddingDp: Int,
    val statsHorizontalPaddingDp: Int,
    val statsVerticalPaddingDp: Int,
    val statsCornerRadiusDp: Int,
    val statsFontSp: Int,
    val centerPlayButtonSizeDp: Int,
    val centerPlayInnerButtonSizeDp: Int,
    val centerPlayIconSizeDp: Int,
    val qualitySwitchCornerRadiusDp: Int,
    val qualitySwitchOuterPaddingDp: Int,
    val qualitySwitchContentHorizontalPaddingDp: Int,
    val qualitySwitchContentVerticalPaddingDp: Int,
    val qualitySwitchContentSpacingDp: Int,
    val qualitySwitchMessageFontSp: Int,
    val interactionIconSizeDp: Int,
    val interactionLabelFontSp: Int,
    val interactionLabelTopSpacingDp: Int,
    val tripleActionSpacingDp: Int,
    val tripleRingExtraSizeDp: Int
)

fun resolveVideoPlayerOverlayVisualPolicy(
    widthDp: Int
): VideoPlayerOverlayVisualPolicy {
    // VR 面板：中央播放大按钮 + 锁定/互动大图标（docs/vr/gesture-comfort.md §2.1）
    if (VrPanelRuntimeFlags.activityEmbeddedInVrPanel) {
        return VideoPlayerOverlayVisualPolicy(
            topScrimHeightDp = 240,
            bottomScrimHeightDp = 320,
            lockButtonEndPaddingDp = 48,
            lockButtonSizeDp = 84,
            lockButtonCornerRadiusDp = 22,
            lockIconSizeDp = 40,
            statsTopPaddingDp = 120,
            statsEndPaddingDp = 44,
            statsHorizontalPaddingDp = 16,
            statsVerticalPaddingDp = 8,
            statsCornerRadiusDp = 8,
            statsFontSp = 18,
            centerPlayButtonSizeDp = 128,
            centerPlayInnerButtonSizeDp = 92,
            centerPlayIconSizeDp = 64,
            qualitySwitchCornerRadiusDp = 20,
            qualitySwitchOuterPaddingDp = 40,
            qualitySwitchContentHorizontalPaddingDp = 40,
            qualitySwitchContentVerticalPaddingDp = 24,
            qualitySwitchContentSpacingDp = 20,
            qualitySwitchMessageFontSp = 20,
            interactionIconSizeDp = 40,
            interactionLabelFontSp = 16,
            interactionLabelTopSpacingDp = 8,
            tripleActionSpacingDp = 32,
            tripleRingExtraSizeDp = 24
        )
    }

    if (widthDp >= 1600) {
        return VideoPlayerOverlayVisualPolicy(
            topScrimHeightDp = 200,
            bottomScrimHeightDp = 280,
            lockButtonEndPaddingDp = 40,
            lockButtonSizeDp = 64,
            lockButtonCornerRadiusDp = 16,
            lockIconSizeDp = 30,
            statsTopPaddingDp = 104,
            statsEndPaddingDp = 36,
            statsHorizontalPaddingDp = 12,
            statsVerticalPaddingDp = 6,
            statsCornerRadiusDp = 6,
            statsFontSp = 14,
            centerPlayButtonSizeDp = 108,
            centerPlayInnerButtonSizeDp = 78,
            centerPlayIconSizeDp = 54,
            qualitySwitchCornerRadiusDp = 16,
            qualitySwitchOuterPaddingDp = 32,
            qualitySwitchContentHorizontalPaddingDp = 32,
            qualitySwitchContentVerticalPaddingDp = 20,
            qualitySwitchContentSpacingDp = 16,
            qualitySwitchMessageFontSp = 16,
            interactionIconSizeDp = 28,
            interactionLabelFontSp = 12,
            interactionLabelTopSpacingDp = 6,
            tripleActionSpacingDp = 20,
            tripleRingExtraSizeDp = 16
        )
    }

    if (widthDp >= 840) {
        return VideoPlayerOverlayVisualPolicy(
            topScrimHeightDp = 168,
            bottomScrimHeightDp = 240,
            lockButtonEndPaddingDp = 30,
            lockButtonSizeDp = 56,
            lockButtonCornerRadiusDp = 14,
            lockIconSizeDp = 26,
            statsTopPaddingDp = 92,
            statsEndPaddingDp = 28,
            statsHorizontalPaddingDp = 10,
            statsVerticalPaddingDp = 5,
            statsCornerRadiusDp = 5,
            statsFontSp = 13,
            centerPlayButtonSizeDp = 96,
            centerPlayInnerButtonSizeDp = 68,
            centerPlayIconSizeDp = 48,
            qualitySwitchCornerRadiusDp = 14,
            qualitySwitchOuterPaddingDp = 28,
            qualitySwitchContentHorizontalPaddingDp = 28,
            qualitySwitchContentVerticalPaddingDp = 18,
            qualitySwitchContentSpacingDp = 14,
            qualitySwitchMessageFontSp = 15,
            interactionIconSizeDp = 26,
            interactionLabelFontSp = 11,
            interactionLabelTopSpacingDp = 5,
            tripleActionSpacingDp = 18,
            tripleRingExtraSizeDp = 14
        )
    }

    if (widthDp >= 600) {
        return VideoPlayerOverlayVisualPolicy(
            topScrimHeightDp = 152,
            bottomScrimHeightDp = 220,
            lockButtonEndPaddingDp = 26,
            lockButtonSizeDp = 52,
            lockButtonCornerRadiusDp = 13,
            lockIconSizeDp = 25,
            statsTopPaddingDp = 86,
            statsEndPaddingDp = 26,
            statsHorizontalPaddingDp = 9,
            statsVerticalPaddingDp = 4,
            statsCornerRadiusDp = 4,
            statsFontSp = 12,
            centerPlayButtonSizeDp = 88,
            centerPlayInnerButtonSizeDp = 62,
            centerPlayIconSizeDp = 45,
            qualitySwitchCornerRadiusDp = 13,
            qualitySwitchOuterPaddingDp = 26,
            qualitySwitchContentHorizontalPaddingDp = 26,
            qualitySwitchContentVerticalPaddingDp = 17,
            qualitySwitchContentSpacingDp = 13,
            qualitySwitchMessageFontSp = 14,
            interactionIconSizeDp = 25,
            interactionLabelFontSp = 10,
            interactionLabelTopSpacingDp = 4,
            tripleActionSpacingDp = 17,
            tripleRingExtraSizeDp = 13
        )
    }

    return VideoPlayerOverlayVisualPolicy(
        topScrimHeightDp = 140,
        bottomScrimHeightDp = 200,
        lockButtonEndPaddingDp = 24,
        lockButtonSizeDp = 48,
        lockButtonCornerRadiusDp = 12,
        lockIconSizeDp = 24,
        statsTopPaddingDp = 80,
        statsEndPaddingDp = 24,
        statsHorizontalPaddingDp = 8,
        statsVerticalPaddingDp = 4,
        statsCornerRadiusDp = 4,
        statsFontSp = 12,
        centerPlayButtonSizeDp = 82,
        centerPlayInnerButtonSizeDp = 58,
        centerPlayIconSizeDp = 42,
        qualitySwitchCornerRadiusDp = 12,
        qualitySwitchOuterPaddingDp = 24,
        qualitySwitchContentHorizontalPaddingDp = 24,
        qualitySwitchContentVerticalPaddingDp = 16,
        qualitySwitchContentSpacingDp = 12,
        qualitySwitchMessageFontSp = 14,
        interactionIconSizeDp = 24,
        interactionLabelFontSp = 10,
        interactionLabelTopSpacingDp = 4,
        tripleActionSpacingDp = 16,
        tripleRingExtraSizeDp = 12
    )
}
