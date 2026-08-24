package com.android.purebilibili.quest

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.DpRect
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * 导航条面板布局回归（Robolectric 容器：真实布局 Compose UI，无需头显/模拟器）。
 *
 * 历史 bug：3 个按钮（240dp 宽 ×3）超过 560dp 面板宽度，第三个按钮被裁剪。
 * 这里在面板真实尺寸下渲染 VrControlBar，用 getBoundsInRoot 断言每个按钮
 * 都在面板边界内（left>=0 且 right<=560dp）。
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w560dp-h140dp")
class VrControlBarLayoutRobolectricTest {

    @get:Rule
    val composeRule: ComposeContentTestRule = createComposeRule()

    private val panelWidthDp = 560.dp
    private val panelHeightDp = 140.dp

    private fun renderControlBar() {
        composeRule.setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                VrControlBar(
                    onHome = {},
                    onHide = {},
                    onCycleSize = {},
                    sizeLabel = "画面 · 标准",
                )
            }
        }
    }

    private fun assertWithinPanel(label: String) {
        val node = composeRule.onNodeWithText(label)
        node.assertIsDisplayed()
        val bounds: DpRect = node.getBoundsInRoot()
        val inside =
            bounds.left >= 0.dp && bounds.right <= panelWidthDp &&
                bounds.top >= 0.dp && bounds.bottom <= panelHeightDp
        assert(inside) {
            "「$label」越界: left=${bounds.left} top=${bounds.top} " +
                "right=${bounds.right} bottom=${bounds.bottom}（面板 ${panelWidthDp}x${panelHeightDp}）"
        }
    }

    @Test
    fun `collapsed menu button is displayed within panel`() {
        renderControlBar()
        assertWithinPanel("菜单")
    }

    @Test
    fun `expanded three buttons all fit inside panel width`() {
        renderControlBar()
        composeRule.onNodeWithText("菜单").performClick()

        val buttons = listOf("Quest 主页", "画面 · 标准", "隐藏面板")
        buttons.forEach { label -> assertWithinPanel(label) }
    }
}
