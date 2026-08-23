package com.android.purebilibili.core.util

/**
 * VR 面板运行时状态。默认全 false：手机端与普通 2D 运行完全不受影响。
 * 仅 quest flavor 的 ImmersiveActivity 在挂载面板期间置 true，
 * 用于让播放器方向策略在固定朝向的 VR 面板窗口里跳过 requestedOrientation。
 */
object VrPanelRuntimeFlags {
    @Volatile
    var activityEmbeddedInVrPanel: Boolean = false
}
