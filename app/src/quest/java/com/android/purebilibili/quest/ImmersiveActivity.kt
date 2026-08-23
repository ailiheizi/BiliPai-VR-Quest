package com.android.purebilibili.quest

import android.content.Intent
import android.util.Log
import com.android.purebilibili.MainActivity
import com.android.purebilibili.R
import com.meta.spatial.compose.ComposeFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.isdk.IsdkFeature
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.QuadLayerConfig
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.spatial.vr.VRFeature

/**
 * BiliPai VR 沉浸入口（Quest 应用库启动项）。
 *
 * 结构对照官方 MediaSpatialAppTemplate codelab：
 * - AppSystemActivity + VRFeature + ComposeFeature + IsdkFeature
 * - PanelRegistration.panelIntent 指向现有 MainActivity（手机端零改动），渲染成曲面面板
 * - onSceneReady 里 Entity.createPanelEntity 放置面板
 *
 * Phase 2 的舒适手势改造都在面板参数（距离/尺寸/dpi）与 quest 专属 UI 上做。
 */
class ImmersiveActivity : AppSystemActivity() {

    private companion object {
        const val TAG = "BiliPaiVR"
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
        scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)
        Log.d(TAG, "onSceneReady: scheduling main panel spawn")

        // 官方模板配方：createPanelEntity + 身份旋转。
        // 延迟到主线程队列空闲后创建，确保 registerPanels() 注册已就位（否则
        // PanelCreationSystem 找不到注册会静默跳过）。
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(
            {
                val panelEntity =
                    Entity.createPanelEntity(
                        R.id.bilipai_main_panel,
                        Transform(Pose(Vector3(x = 0f, y = 1.3f, z = 2f), Quaternion(0f, 0f, 0f))),
                    )
                Log.d(TAG, "panel spawned late: id=${panelEntity.id}")
            },
            1500,
        )
    }

    override fun registerPanels(): List<PanelRegistration> {
        return listOf(
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
        )
    }
}
