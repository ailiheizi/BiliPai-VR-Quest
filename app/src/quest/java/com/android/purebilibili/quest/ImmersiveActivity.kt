package com.android.purebilibili.quest

import android.content.Intent
import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.android.purebilibili.MainActivity
import com.android.purebilibili.R
import com.android.purebilibili.core.util.VrPanelRuntimeFlags
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.compose.composePanel
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.QuadLayerConfig
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature

/** hover 放大系数（docs/vr/gesture-comfort.md §2.2） */
private const val CONTROL_HOVER_SCALE = 1.06f

/**
 * BiliPai VR 沉浸入口（Quest 应用库启动项）。
 *
 * 结构对照官方 MediaSpatialAppTemplate codelab：
 * - AppSystemActivity + VRFeature + ComposeFeature + IsdkFeature
 * - PanelRegistration.panelIntent 指向现有 MainActivity（手机端零改动），渲染成曲面面板
 * - 主面板下方浮一条 VR 导航条（composePanel）：Quest 主页 / 隐藏面板，大控件 + hover 反馈
 *
 * 面板实体延迟创建：registerPanels() 完成晚于 onSceneReady，过早创建会被
 * PanelCreationSystem 静默跳过（实测 Horizon OS v75+）。
 */
class ImmersiveActivity : AppSystemActivity() {

    private companion object {
        const val TAG = "BiliPaiVR"
        const val PANEL_SPAWN_DELAY_MS = 1500L
    }

    override fun registerFeatures(): List<SpatialFeature> {
        return listOf(
            VRFeature(this),
            ComposeFeature(),
            // ISDK：手部射线 + Pinch、手柄激光自动翻译成 Android touch（面板内无需自写手势）
            IsdkFeature(this, spatial, systemManager),
        )
    }

    override fun onSceneReady() {
        super.onSceneReady()
        // 面板窗口朝向固定，禁止播放器在面板里旋转（手机端不受影响）
        VrPanelRuntimeFlags.activityEmbeddedInVrPanel = true
        scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)
        Log.d(TAG, "onSceneReady: scheduling panel spawn")

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
            { spawnPanels() },
            PANEL_SPAWN_DELAY_MS,
        )
    }

    override fun onDestroy() {
        VrPanelRuntimeFlags.activityEmbeddedInVrPanel = false
        super.onDestroy()
    }

    private fun spawnPanels() {
        val mainPanel =
            Entity.createPanelEntity(
                R.id.bilipai_main_panel,
                Transform(Pose(Vector3(x = 0f, y = 1.35f, z = 2f), Quaternion(0f, 0f, 0f))),
            )
        Log.d(TAG, "main panel spawned id=${mainPanel.id}")

        // 导航条：主面板正下方偏近处，抬头可见、垂手可及
        val controlBar =
            Entity.createPanelEntity(
                R.id.bilipai_control_bar,
                Transform(Pose(Vector3(x = 0f, y = 0.45f, z = 1.7f), Quaternion(0f, 0f, 0f))),
                Grabbable(),
            )
        Log.d(TAG, "control bar spawned id=${controlBar.id}")
    }

    override fun registerPanels(): List<PanelRegistration> {
        return listOf(mainPanelRegistration(), controlBarRegistration())
    }

    /** 主面板：现有 MainActivity 原样渲染（手机端零改动）。 */
    private fun mainPanelRegistration(): PanelRegistration =
        PanelRegistration(R.id.bilipai_main_panel) {
            panelIntent =
                Intent().apply {
                    setClassName(applicationContext, MainActivity::class.qualifiedName!!)
                }
            config {
                height = 1.9f
                width = 3.0f
                layoutWidthInPx = 1600
                layoutHeightInPx = 1000
                layerConfig = QuadLayerConfig()
                panelShader = SceneMaterial.HOLE_PUNCH_SHADER
                alphaMode = AlphaMode.HOLE_PUNCH
            }
        }

    /** VR 导航条：大按钮 + hover 反馈，解决"回不到系统主页"的痛点。 */
    private fun controlBarRegistration(): PanelRegistration =
        PanelRegistration(R.id.bilipai_control_bar) {
            config {
                themeResourceId = R.style.PanelAppThemeTransparent
                layoutWidthInDp = 560f
                layoutHeightInDp = 140f
                layerConfig = LayerConfig()
                enableTransparent = true
                includeGlass = false
            }
            composePanel {
                setContent {
                    MaterialTheme(colorScheme = darkColorScheme()) {
                        VrControlBar(
                            onHome = { launchSystemHome() },
                            onHide = { moveTaskToBack(true) },
                        )
                    }
                }
            }
        }

    private fun launchSystemHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN)
                .addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }
}

/**
 * VR 导航条 UI。舒适规则（docs/vr/gesture-comfort.md）：
 * - 控件高 96dp+、间距 24dp+
 * - hover 即放大高亮（ISDK 把射线悬停翻译成 hover 事件）
 */
@Composable
fun VrControlBar(onHome: () -> Unit, onHide: () -> Unit) {
    Row(
        modifier = Modifier.padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ControlButton(label = "Quest 主页", icon = Icons.Filled.Home, onClick = onHome)
        ControlButton(label = "隐藏面板", icon = Icons.AutoMirrored.Filled.ArrowBack, onClick = onHide)
    }
}

@Composable
private fun ControlButton(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val scale by animateFloatAsState(
        targetValue = if (hovered) CONTROL_HOVER_SCALE else 1f,
        animationSpec = tween(durationMillis = 90),
        label = "hoverScale",
    )

    Surface(
        modifier = Modifier
            .width(240.dp)
            .height(104.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
        shape = RoundedCornerShape(28.dp),
        color = if (hovered) Color(0xFF2D5BFF) else Color(0xCC1C1C22),
        border = if (hovered) BorderStroke(2.dp, Color.White.copy(alpha = 0.85f)) else null,
        onClick = onClick,
        interactionSource = interactionSource,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.width(34.dp).height(34.dp),
            )
            Text(
                text = label,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
            )
        }
    }
}
