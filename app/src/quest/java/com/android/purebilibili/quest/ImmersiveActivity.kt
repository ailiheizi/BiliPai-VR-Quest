package com.android.purebilibili.quest

import com.android.purebilibili.MainActivity
import com.android.purebilibili.R
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.toolkit.ActivityPanelRegistration
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.DpDisplayOptions
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.QuadShapeOptions
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.UIPanelSettings
import com.meta.spatial.vr.VRFeature

/**
 * BiliPai VR 沉浸入口（Quest 应用库启动项）。
 *
 * 结构照搬官方 HybridSample / MediaPlayerSample：
 * - AppSystemActivity + VRFeature + ComposeFeature + IsdkFeature
 * - 用 ActivityPanelRegistration 把现有 MainActivity（手机端零改动）挂成曲面面板
 * - 面板实体直接代码创建，不依赖 Spatial Editor 场景文件
 *
 * Phase 2 的舒适手势改造都在面板参数（距离/尺寸/dpi）与 quest 专属 UI 上做。
 */
class ImmersiveActivity : AppSystemActivity() {

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
        scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

        // 舒适基线（docs/vr/ux-design.md §5）：1.5-2.5m 距离、眼高 ~1.4m、固定锚定
        Entity.create(
            listOf(
                Panel(R.id.bilipai_main_panel),
                Transform(Pose(Vector3(x = 0f, y = 1.4f, z = -2.0f))),
            )
        )
    }

    override fun registerPanels(): List<PanelRegistration> {
        return listOf(
            ActivityPanelRegistration(
                R.id.bilipai_main_panel,
                classIdCreator = { MainActivity::class.java },
                settingsCreator = {
                    UIPanelSettings(
                        // 物理 3.0m x 1.9m 影院屏；1280x810dp @110dpi 保证文字可读且不过度耗 GPU
                        shape = QuadShapeOptions(width = 3.0f, height = 1.9f),
                        display = DpDisplayOptions(width = 1280f, height = 810f, dpi = 110),
                    )
                },
            )
        )
    }
}
