package com.android.purebilibili.quest

import com.android.purebilibili.core.util.VrPanelRuntimeFlags
import com.android.purebilibili.feature.video.ui.overlay.resolveBottomControlBarLayoutPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * 播放器底栏 VR 溢出回归（基于真实策略数据的 VR 分支）。
 *
 * 历史风险：VR 大控件曾把右侧按钮挤出面板窗口导致"点了没反应"。
 * 这里置位 VrPanelRuntimeFlags 后取真实 BottomControlBarLayoutPolicy 的 VR 分支，
 * 断言固定宽度元素（播放键/弹幕设置键/全屏键 + 内边距 + 间距）在 1280dp
 * 面板内容宽度内，并为文本部分留足余量。
 */
class VrPlayerBottomBarOverflowPolicyTest {

    private val panelWidthDp = 1280

    @BeforeEach
    fun enableVr() {
        VrPanelRuntimeFlags.activityEmbeddedInVrPanel = true
    }

    @AfterEach
    fun disableVr() {
        VrPanelRuntimeFlags.activityEmbeddedInVrPanel = false
    }

    @Test
    fun `vr branch is active when flag set`() {
        val policy = resolveBottomControlBarLayoutPolicy(widthDp = panelWidthDp)
        // VR 分支播放键 60dp；普通 1280 档是 40dp —— 用这个区分分支生效
        assertTrue(
            policy.playButtonSizeDp >= 60,
            "VR 分支未生效，playButtonSizeDp=${policy.playButtonSizeDp}（期望 ≥60）"
        )
    }

    @Test
    fun `vr bottom bar fixed widths fit inside panel content width with text allowance`() {
        val vrPolicy = resolveBottomControlBarLayoutPolicy(widthDp = panelWidthDp)
        // 固定宽度贡献（dp）：
        val fixed =
            vrPolicy.playButtonSizeDp +
                vrPolicy.horizontalPaddingDp * 2 +
                vrPolicy.danmakuSettingButtonSizeDp +
                vrPolicy.fullscreenIconSizeDp +
                vrPolicy.danmakuSettingEndPaddingDp +
                vrPolicy.rightActionSpacingDp * 3 + // 三段间距保守估
                vrPolicy.progressSpacingDp

        // 文本/可变部分（时间、倍速、画质、弹幕开关文字）留 500dp 余量，
        // 1280dp 窗口下最保守的防溢出上界。VR 数值若增大导致超限，本测试先报警。
        val textAllowance = 500
        val total = fixed + textAllowance
        val contentWidth = panelWidthDp - vrPolicy.horizontalPaddingDp * 2

        assertTrue(
            total <= contentWidth,
            "VR 底栏固定宽度 $fixed + 文本余量 $textAllowance = $total " +
                "超出内容宽 $contentWidth，右侧控件可能被挤出面板窗口"
        )
    }
}
