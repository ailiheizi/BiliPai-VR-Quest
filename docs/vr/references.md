# 参考项目与技术调研

> 本文件记录做 BiliPai VR 时所有值得参考的项目、库、代码位置，按"直接可用 / 抄作业 / 参考设计"三级分类。

---

## 一、可直接用的官方工程（抄作业）

### 1. Meta-Spatial-SDK-Samples（首选参考）
- 地址：`github.com/meta-quest/Meta-Spatial-SDK-Samples`（官方，MIT）
- 作用：Meta Spatial SDK 官方样例，展示把 Android/Compose 应用渲染成 VR 空间面板
- **HybridSample** —— 最核心的参考
  - 位置：`HybridSample/app/src/main/java/com/meta/spatial/samples/hybridsample/`
  - `HybridSampleActivity.kt`：继承 `AppSystemActivity`，`registerFeatures()` 注册 `VRFeature` + `ComposeFeature`；`launchPanelModeInHome()` 用 `PanelRegistration` + `PendingIntent` 把 `PancakeActivity` 渲染成 3D 面板
  - `PancakeActivity.kt`：标准的 2D Compose Activity，被当作面板内容
  - `build.gradle.kts`：展示了 Spatial SDK 全部依赖清单（`meta.spatial.sdk.base / .compose / .vr / .toolkit / .isdk / .castinputforward / .hotreload` 等）
- **MediaPlayerSample** —— 媒体播放参考
  - 位置：`MediaPlayerSample/app/src/main/java/com/meta/spatial/samples/mediaplayersample/`
  - `MediaPlayerSampleActivity.kt` + `ListPanel.kt` / `MRPanel.kt`：视频列表 + 播放面板
  - **最适合参考"B站视频列表 + 播放"的 VR 形态**
- 版本要求：Gradle 9.4.1、AGP 8.11.1、Kotlin 2.1.0、JDK 17、Quest 固件 v69+、minSdk 34

### 2. Meta-Spatial-SDK-Samples 其他样例
- `CustomComponentsSample`：自定义 VR 组件
- `AnimationsSample`：3D 动画
- `FeatureDevSample`：feature 开发模板
- `BodyTrackingSample`：身体追踪（可参考未来加入全身/手势）

### 3. fbsamples/MediaSpatialAppTemplate（备选参考）
- 地址：`github.com/fbsamples/MediaSpatialAppTemplate`
- 作用：Meta 官方"2D Panel Mode + Immersive Mode"双模式媒体应用模板，Kotlin，带 codelab
- 参考价值：**双模式切换**（手机/平板 2D 模式 ↔ VR 沉浸模式）的实现方式，与 BiliPai 做 `mobile/quest` 双 flavor 的诉求一致

---

## 二、可参考的交互与设计（YouTube VR 范式）

### YouTube VR 的交互设计要点（核心参考）
- **主页 = 环绕视频墙**：内容以 3D 环绕方式排列，转头/手柄选择，不用精细点击
- **播放页 = 大屏 + 极简控制**：播放/暂停是大按钮，进度条大滑块，音量/清晰度是清晰的大控件
- **手部射线 + 凝视确认**：手柄激光指针或手部射线指向目标，Pinch/扳机确认
- **极简 UI**：VR 里小控件（触屏那种）完全不可用，一切控件都要放大、拉开间距
- **输入**：搜索/文字输入用 VR 键盘（系统级）或语音，避免触屏键盘

### 设计原则迁移到 BiliPai
| 手机 UI（现有） | VR UI（要改造成） |
| --- | --- |
| 密集列表、小卡片 | 大卡片、环绕视频墙、间距拉大 |
| 小按钮、小图标 | 大按钮、大图标、48dp+ 触控目标翻倍 |
| 底部导航小图标 | 侧边或环绕大图标导航 |
| 触屏手势（滑动、拖动） | 手部射线指向 + 扳机/Pinch 确认 |
| 触屏键盘 | VR 系统键盘 / 语音输入 / 扫码登录 |

---

## 三、Quest 侧载与工具链参考

| 工具 | 用途 | 参考 |
| --- | --- | --- |
| SideQuest | 侧载 APK 到 Quest | sidequestvr.com |
| Wolvic | 开源 VR 浏览器，可先刷 B站网页验证体验 | `github.com/Igalia/wolvic`（MPL-2.0） |
| Meta OpenXR SDK | 原生 C++/OpenXR 开发（本方案不用，仅背景） | `github.com/meta-quest/Meta-OpenXR-SDK` |
| ADB | 安装/调试/截图 | Quest 已连（`1WMHH86ARM1335`） |

---

## 四、同类 B站客户端横向对比

| 项目 | 技术栈 | 手柄/TV 支持 | 活跃度 | 对 VR 方案的价值 |
| --- | --- | --- | --- | --- |
| **BiliPai**（本仓库） | Kotlin/Compose | 无 TV，仅平板 | 很活跃 | **主目标**，与 Spatial SDK 同构 |
| **PiliPlus** | Flutter | 无 TV | 很活跃 | 有 TV fork 的 D-pad 焦点代码可借鉴，但被排除（Flutter 不兼容 Panel 渲染） |
| **BBLL** | 原生 | ✅ TV/D-pad 适配好 | 停更 | 交互范式参考（遥控器导航），源码未公开 |
| keluokeda/Bilibili | Kotlin | ✅ 有 TV 模块 | 中等 | TV 模块实现参考 |
| PiliPlus-Tizen / UcnacDx2 TV | Flutter | ✅ D-pad 焦点 | - | Focus 包装代码可参考（若以后需要 TV 形态） |

---

## 五、关键技术点速查

### HybridSample 的核心调用链（写代码时抄这里）
```kotlin
// 1. 继承 AppSystemActivity，注册 VR + Compose feature
class XxxActivity : AppSystemActivity() {
  override fun registerFeatures() = listOf(
    VRFeature(this),          // 沉浸 VR 渲染
    ComposeFeature(),         // Compose UI 渲染支持
  )
}

// 2. 把某个 Compose Activity 注册成 VR 面板
val panelIntent = Intent(this, PancakeActivity::class.java).apply {
  action = Intent.ACTION_MAIN
  addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
}
val pendingPanelIntent = PendingIntent.getActivity(this, 0, panelIntent, ...)
// 用 PanelRegistration + createPanelEntity 放到场景里（见 HybridSample 完整代码）
```

### Spatial SDK 依赖（加到 quest flavor 的 build.gradle.kts）
```kotlin
implementation("com.meta.spatial:meta-spatial-sdk-base:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-compose:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-vr:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-toolkit:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-isdk:0.5+")
implementation("com.meta.spatial:meta-spatial-sdk-castinputforward:0.5+")
// 其余（ovrmetrics / hotreload / datamodelinspector / uiset）视需要
```

### 版本兼容性提示
- BiliPai 当前：AGP 9.3.1、Kotlin 2.4.0、JDK 21、minSdk 26、compileSdk 37
- Spatial SDK Samples：AGP 8.11.1、Kotlin 2.1.0、JDK 17、minSdk 34、targetSdk 34
- **冲突点**：BiliPai 用的 AGP/Kotlin 比官方样例新。需实测：AGP 9.x 是否能正常构建 Spatial SDK 依赖。若有问题，`quest` flavor 可考虑降低到样例的 toolchain 版本（用独立 gradle wrapper 或降 Kotlin），但**不要动 mobile flavor**。
