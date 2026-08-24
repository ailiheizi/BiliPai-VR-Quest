package com.android.purebilibili.feature.video.ui.overlay

import com.android.purebilibili.core.util.VrPanelRuntimeFlags

data class VideoProgressBarLayoutPolicy(
    val baseHeightWithoutChapterDp: Int,
    val baseHeightWithChapterDp: Int,
    val draggingContainerHeightDp: Int,
    val previewBottomPaddingDp: Int,
    val chapterBottomPaddingDp: Int,
    val chapterStartPaddingDp: Int,
    val chapterIconSizeDp: Int,
    val chapterSpacingDp: Int,
    val chapterFontSp: Int,
    val touchContainerHeightDp: Int,
    val trackHeightDp: Float,
    val thumbIdleSizeDp: Int,
    val thumbDraggingSizeDp: Int,
    val thumbIdleOffsetDp: Int,
    val thumbDraggingOffsetDp: Int
)

fun resolveVideoProgressBarLayoutPolicy(
    widthDp: Int
): VideoProgressBarLayoutPolicy {
    // VR 面板：进度条加粗加高、thumb 放大（docs/vr/gesture-comfort.md §2.1）
    if (VrPanelRuntimeFlags.activityEmbeddedInVrPanel) {
        return VideoProgressBarLayoutPolicy(
            baseHeightWithoutChapterDp = 40,
            baseHeightWithChapterDp = 56,
            draggingContainerHeightDp = 160,
            previewBottomPaddingDp = 38,
            chapterBottomPaddingDp = 10,
            chapterStartPaddingDp = 28,
            chapterIconSizeDp = 24,
            chapterSpacingDp = 12,
            chapterFontSp = 18,
            touchContainerHeightDp = 40,
            trackHeightDp = 8f,
            thumbIdleSizeDp = 24,
            thumbDraggingSizeDp = 34,
            thumbIdleOffsetDp = 13,
            thumbDraggingOffsetDp = 19
        )
    }

    if (widthDp >= 1600) {
        return VideoProgressBarLayoutPolicy(
            baseHeightWithoutChapterDp = 32,
            baseHeightWithChapterDp = 48,
            draggingContainerHeightDp = 140,
            previewBottomPaddingDp = 30,
            chapterBottomPaddingDp = 6,
            chapterStartPaddingDp = 20,
            chapterIconSizeDp = 16,
            chapterSpacingDp = 8,
            chapterFontSp = 14,
            touchContainerHeightDp = 28,
            trackHeightDp = 3.5f,
            thumbIdleSizeDp = 14,
            thumbDraggingSizeDp = 20,
            thumbIdleOffsetDp = 8,
            thumbDraggingOffsetDp = 10
        )
    }

    if (widthDp >= 840) {
        return VideoProgressBarLayoutPolicy(
            baseHeightWithoutChapterDp = 26,
            baseHeightWithChapterDp = 40,
            draggingContainerHeightDp = 120,
            previewBottomPaddingDp = 26,
            chapterBottomPaddingDp = 5,
            chapterStartPaddingDp = 16,
            chapterIconSizeDp = 14,
            chapterSpacingDp = 6,
            chapterFontSp = 12,
            touchContainerHeightDp = 24,
            trackHeightDp = 3f,
            thumbIdleSizeDp = 12,
            thumbDraggingSizeDp = 16,
            thumbIdleOffsetDp = 7,
            thumbDraggingOffsetDp = 9
        )
    }

    if (widthDp >= 600) {
        return VideoProgressBarLayoutPolicy(
            baseHeightWithoutChapterDp = 23,
            baseHeightWithChapterDp = 36,
            draggingContainerHeightDp = 110,
            previewBottomPaddingDp = 25,
            chapterBottomPaddingDp = 4,
            chapterStartPaddingDp = 14,
            chapterIconSizeDp = 13,
            chapterSpacingDp = 5,
            chapterFontSp = 11,
            touchContainerHeightDp = 22,
            trackHeightDp = 2.5f,
            thumbIdleSizeDp = 11,
            thumbDraggingSizeDp = 15,
            thumbIdleOffsetDp = 6,
            thumbDraggingOffsetDp = 8
        )
    }

    return VideoProgressBarLayoutPolicy(
        baseHeightWithoutChapterDp = 20,
        baseHeightWithChapterDp = 32,
        draggingContainerHeightDp = 100,
        previewBottomPaddingDp = 24,
        chapterBottomPaddingDp = 4,
        chapterStartPaddingDp = 12,
        chapterIconSizeDp = 12,
        chapterSpacingDp = 4,
        chapterFontSp = 10,
        touchContainerHeightDp = 20,
        trackHeightDp = 2.25f,
        thumbIdleSizeDp = 10,
        thumbDraggingSizeDp = 14,
        thumbIdleOffsetDp = 6,
        thumbDraggingOffsetDp = 8
    )
}
