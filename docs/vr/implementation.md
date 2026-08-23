# Phase 1-2 — 核心实现（quest flavor + Spatial SDK 接入）

> 目标：把 BiliPai 以 VR 曲面面板形态跑起来（Phase 1），再做成手柄/手势友好的体验（Phase 2）。
> 参考主线：`Meta-Spatial-SDK-Samples/HybridSample`（代码几乎照搬），BiliPai 只改入口与构建，业务代码不动。

---

## Phase 1 — 最小 Demo（Quest 上能刷 B站视频）

### 1.1 新增 `quest` productFlavor

在 `app/build.gradle.kts`（`/Users/macos/Documents/other_project/bilipai-vr/app/build.gradle.kts`）：

```kotlin
android {
  flavorDimensions += "platform"
  productFlavors {
    create("mobile") {
      dimension = "platform"
      // 现有默认，什么都不改
    }
    create("quest") {
      dimension = "platform"
      applicationId = "com.android.purebilibili.quest"  // 独立包名，避免与手机版冲突
      minSdk = 34   // Spatial SDK 要求（Horizon OS = API 34）
    }
  }
}
```

> 注意：BiliPai 当前 minSdk 26 / targetSdk 35 / compileSdk 37。`quest` flavor 的 minSdk 拉到 34 即可，target/compile 保持。**必须实测 AGP 9.3.1 + Kotlin 2.4 能否构建 Spatial SDK 依赖**，不行就参考 `references.md` 的 toolchain 兼容性提示。

### 1.2 加 Spatial SDK 依赖

在 `quest` flavor 相关依赖区加入（版本见 `references.md`）：
```kotlin
// quest flavor 专用
implementation("com.meta.spatial:meta-spatial-sdk-base:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-compose:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-vr:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-toolkit:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-isdk:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-castinputforward:0.5+")
```

### 1.3 新增 Immersive Activity

新建 `app/src/quest/java/com/android/purebilibili/quest/ImmersiveActivity.kt`，照搬 HybridSample 结构：

```kotlin
class ImmersiveActivity : AppSystemActivity() {
  override fun registerFeatures(): List<SpatialFeature> {
    return listOf(
      VRFeature(this),        // 沉浸 VR
      ComposeFeature(),       // Compose 渲染
    )
  }

  override fun onSceneReady() {
    super.onSceneReady()
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)
    // 设置光照 / 环境 / 原点（照抄 HybridSample onSceneReady）
  }

  fun launchBiliPaiPanel() {
    val panelIntent = Intent(this, MainActivity::class.java).apply {
      action = Intent.ACTION_MAIN
      addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val pendingPanelIntent = PendingIntent.getActivity(this, 0, panelIntent, ...)
    // PanelRegistration + createPanelEntity：把 BiliPai MainActivity 渲染成曲面面板
  }
}
```

关键点：
- `MainActivity.kt`（BiliPai 现有入口）**零改动**，只是被当作 panel 内容加载
- `PanelRegistration` 可调尺寸/曲率（`QuadLayerConfig`），决定"屏幕"大小和弧度

### 1.4 quest flavor 的 Manifest

新建 `app/src/quest/AndroidManifest.xml`，只追加 ImmersiveActivity 声明：
```xml
<activity android:name=".quest.ImmersiveActivity" android:exported="true">
  <intent-filter>
    <action android:name="android.intent.action.MAIN" />
    <category android:name="com.oculus.intent.category.VR" />
  </intent-filter>
</activity>
```

> `com.oculus.intent.category.VR` 是关键：这样它才会出现在 Quest 的 VR 应用库里（对照 Wolvic 之前"找不到"的教训）。

### 1.5 构建 + 侧载 + 启动

```bash
# 构建 quest flavor（debug）
cd /Users/macos/Documents/other_project/bilipai-vr
./gradlew :app:assembleQuestDebug

# 侧载（务必 -s 指定 Quest，别装进小米手机）
adb -s 1WMHH86ARM1335 install -r \
  app/build/outputs/apk/quest/debug/app-quest-debug.apk

# 启动
adb -s 1WMHH86ARM1335 shell am start \
  -n com.android.purebilibili.quest/.quest.ImmersiveActivity
```

### 1.6 验收（Phase 1 完成）
- [ ] Quest 应用库出现 BiliPai（VR 类别）
- [ ] 启动进入 3D 场景，面前有可交互面板
- [ ] 面板里能看 BiliPai 首页
- [ ] 点进视频能播放、有声音
- [ ] 手柄激光/手部追踪能点击、滚动

---

## Phase 2 — 交互打磨（参考 YouTube VR）

> 详见 `ux-design.md`。这里列工程动作。

### 2.1 播放页 VR 化
- 用 `PanelRegistration` 把**视频播放页**单独放大为一个更大的面板（影院感）
- 播放控制改大按钮：播放/暂停、进度条、音量、清晰度、倍速，全部 48dp+ 目标
- 参考 `MediaPlayerSample/MRPanel.kt` 的面板布局

### 2.2 主页视频墙
- 从密集列表 → 大卡片网格（环绕式或 3 列大卡片）
- 参考 YouTube VR 的环绕选择；Spatial SDK 里可用多面板或大网格实现

### 2.3 输入优化
- 搜索/登录用 VR 系统键盘（Horizon OS 提供）或扫码登录
- 减少打字场景

### 2.4 弹幕在 VR
- 弹幕渲染在面板内即可，主要是字号/滚动速度调到 VR 舒适
- 若面板太小，弹幕可考虑铺在面板外 3D 空间（进阶）

### 2.5 双模式
- 保留 `mobile` flavor 手机版不变
- `quest` flavor 是纯 VR 版；以后可参考 `MediaSpatialAppTemplate` 加"同一包双模式"

### 2.6 验收（Phase 2 完成）
- [ ] 播放控制大按钮可点、进度可拖
- [ ] 主页卡片墙可环绕/激光选择
- [ ] 搜索能打字（VR 键盘）或扫码
- [ ] 手柄 + 手势双通道都能操作
- [ ] 长时间使用不晕（面板距离/曲率舒适）

---

## 代码改动清单（预估）

| 文件 | 动作 | 说明 |
| --- | --- | --- |
| `app/build.gradle.kts` | 改 | 加 `quest` flavor + Spatial 依赖 |
| `app/src/quest/AndroidManifest.xml` | 新增 | ImmersiveActivity + VR 类别 |
| `app/src/quest/.../ImmersiveActivity.kt` | 新增 | 照搬 HybridSample |
| `app/src/quest/.../VrPanel.kt` 等 | 新增 | Panel 注册、场景配置 |
| `MainActivity.kt` | 不改（初期） | 作为 panel 内容 |
| 播放页/首页 Composable | Phase 2 改 | VR 化 UI |
| 主题/字号 | Phase 2 改 | VR 舒适字号 |
