package com.android.purebilibili.core.util

import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Build

internal const val LARGE_SCREEN_SMALLEST_WIDTH_DP = 600

internal fun shouldRequestPhysicalPlayerOrientation(
    smallestScreenWidthDp: Int,
    platformIgnoresLargeScreenOrientationRequests: Boolean =
        Build.VERSION.SDK_INT >= 36,
): Boolean {
    val isLargeScreen = smallestScreenWidthDp >= LARGE_SCREEN_SMALLEST_WIDTH_DP
    return !isLargeScreen || !platformIgnoresLargeScreenOrientationRequests
}

/**
 * Android 16+ ignores orientation restrictions for target-36+ apps on large screens,
 * and target 37 removes the manifest opt-out. Older Android releases still honor
 * requestedOrientation on tablets, so do not discard a user's fullscreen request there.
 */
internal fun Activity.applyPlayerRequestedOrientation(requestedOrientation: Int): Boolean {
    // VR 面板窗口朝向固定（横屏 quad），任何 requestedOrientation 都会把面板
    // 内容转成"横着的竖屏"并破坏触摸映射，直接忽略。
    if (VrPanelRuntimeFlags.activityEmbeddedInVrPanel) return false
    val effectiveOrientation = if (
        shouldRequestPhysicalPlayerOrientation(resources.configuration.smallestScreenWidthDp)
    ) {
        requestedOrientation
    } else {
        ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
    if (this.requestedOrientation == effectiveOrientation) return false
    this.requestedOrientation = effectiveOrientation
    return true
}
