# BiliPai VR 改造计划

> 目标：把 BiliPai（Kotlin + Jetpack Compose 的 Bilibili 第三方客户端）改造成可以在 Meta Quest 2/3 独立运行的 VR 版本。
> 核心思路：**不重写 UI、不写 OpenXR 底层渲染**，而是用 Meta 官方 **Spatial SDK** 把现有 Compose 页面渲染成 VR 里的曲面面板（Panel），交互交给 Horizon OS 处理，UI 逐步重做成 VR 友好的形态。

---

## 一、为什么走这条路

前期的调研结论（详见 `references.md`）：

1. **Meta Spatial SDK 是官方正式方案**。它能把普通 Android Activity（含 Compose 页面）直接渲染成 3D 场景里的曲面面板，让"BiliPai 跑在 VR 里"成为可能，且**不用写任何 OpenXR/C++ 渲染代码**。
2. **BiliPai 是 Kotlin/Compose，与 Spatial SDK 官方样板（HybridSample / MediaPlayerSample）技术栈完全同构**，抄作业成本最低。
3. **手部追踪（Hand Tracking）与手柄激光交互由 Horizon OS 自动处理**，App 侧不需要自己实现输入映射。
4. **PiliPlus（Flutter）被排除**：Flutter 渲染引擎不在 Spatial SDK 的 Panel 渲染支持路径内，桥接成本高。

---

## 二、总体技术路线

```
BiliPai 现有代码（手机 UI、B站 API、播放器 Media3、弹幕、插件）
        │
        │ 加一个 quest 构建变体（productFlavor），不动手机端
        ▼
Meta Spatial SDK（VRFeature + ComposeFeature + PanelRegistration）
        │
        │ 把 BiliPai 主界面注册成 VR 曲面面板
        ▼
Quest 2/3 独立运行（Horizon OS 处理手柄激光 + 手部追踪）
```

- **新增**：`quest` productFlavor、一个 `ImmersiveActivity`、Scene/面板注册代码
- **复用**：BiliPai 全部业务代码（取流、播放、弹幕、搜索、登录、收藏）
- **不碰**：现有手机端 UI 与构建

---

## 三、分阶段计划

### Phase 0 — 环境准备（2-3 天）

| 任务 | 说明 | 参考 |
| --- | --- | --- |
| 检查 Quest 固件 | 需 Horizon OS **v69+** | Meta Spatial SDK Samples README |
| 注册 Meta 开发者应用 | 获取 appId / appSecret，配置 Oculus 登录 | Meta 开发者中心 |
| 安装 Meta Spatial Editor | 用于编辑 .metaspatial 场景（可选） | developers.meta.com |
| 配置本机工具链 | 见 `environment.md` | HybridSample build.gradle.kts |
| 跑通官方 HybridSample | 侧载到 Quest 确认 SDK 链路可用 | `Meta-Spatial-SDK-Samples/HybridSample` |

### Phase 1 — 最小 Demo（4-7 天）

把 BiliPai 以"VR 曲面面板"形态跑起来，能刷 B站视频。

1. 复制上游仓库为 `quest` flavor（`productFlavors { mobile / quest }`）
2. 引入 Meta Spatial SDK 依赖（`com.meta.spatial:meta-spatial-sdk:*`）
3. 新增 `ImmersiveActivity`（继承 `AppSystemActivity`），注册 `VRFeature` + `ComposeFeature`
4. 用 `PanelRegistration` 把 BiliPai 主界面注册成面板
5. 侧载到 Quest，验证：能看首页、能点进视频、能播放

### Phase 2 — 交互打磨（4-7 天）

参考 YouTube VR 的交互范式（详见 `ux-design.md`）：

1. 视频播放页改造：大按钮、大进度条、大音量/清晰度控件，手部射线友好
2. 面板大小/曲率/距离调优（`PanelRegistration` 的尺寸、`QuadLayerConfig`）
3. 主页从"列表"逐步改成"视频墙/卡片墙"（环绕式选择）
4. 弹幕在 VR 面板的渲染优化
5. 手势（Pinch/射线）与手柄并行的输入适配

### Phase 3 — 稳定性与收尾（1-2 周）

- 崩溃与热修复
- 登录会话在 Quest 上的保持（之前讨论过的"登录状态丢失"问题）
- 网络/清晰度/解码在 Quest 硬件上的回归
- 性能调优（帧率、内存、发热）
- 打包发布流程（.apk 侧载 / 可选的 Horizon OS 商店提交）

---

## 四、工期总览

| 阶段 | 工期 | 产出 |
| --- | --- | --- |
| Phase 0 | 2-3 天 | 环境就绪，官方样例跑通 |
| Phase 1 | 4-7 天 | Quest 上能刷 B站（demo） |
| Phase 2 | 4-7 天 | 手柄/手势友好，交互可用 |
| Phase 3 | 1-2 周 | 稳定、可日常使用 |
| **总计** | **约 2-3 周 demo，4-6 周全功能** | |

---

## 五、决策记录

| 决策 | 结论 | 理由 |
| --- | --- | --- |
| 基于哪个客户端 | **BiliPai**（非 PiliPlus） | 与 Spatial SDK 同构（Kotlin/Compose），官方样板直接套用 |
| PR 还是 Fork | **Fork，不做 PR** | 上游明确禁止 leanback/TV，VR 改动大概率被拒；`quest` flavor 是独立构建变体 |
| VR 渲染方案 | **Meta Spatial SDK**（非自写 OpenXR） | 官方正式方案，工作量最小，手柄/手势由系统处理 |
| 插件系统是否承载 VR | **否** | 插件只能过滤/美化，无法接管渲染，必须源码级新增 feature |
| 兜底方案 | **Wolvic 浏览器侧载** | 已验证可用，零开发，可作对照与回退 |
