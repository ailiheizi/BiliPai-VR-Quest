package com.android.purebilibili.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.hoverable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * 磁吸悬停：射线/指针悬停时轻微放大（docs/vr/gesture-comfort.md §2.2 吸附规则）。
 *
 * 自包含交互源：触摸设备不产生 hover 事件，因此该修饰符在手机端恒为 1f 缩放、
 * 零视觉与行为影响；仅在产生 hover 的输入通道（VR 射线、鼠标）下生效。
 */
@Composable
fun Modifier.magnetHover(hoveredScale: Float = 1.06f): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (hovered) hoveredScale else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "magnetHoverScale",
    )
    return this
        .hoverable(interactionSource)
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
}
