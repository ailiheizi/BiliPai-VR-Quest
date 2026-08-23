# Phase 0 — 开发环境搭建

> 目标：让本机能构建并侧载 Meta Spatial SDK 应用，先把官方 HybridSample 跑通到 Quest 上，验证工具链。

---

## 0. 本机现状（已确认）

| 项 | 状态 |
| --- | --- |
| Flutter / ADB / Git / gh | ✅ 已装 |
| Quest 2 开发者模式 | ✅ 已开 |
| ADB 连接 Quest 2 | ✅ USB（序列号 `1WMHH86ARM1335`） |
| BiliPai 源码 | ✅ 已克隆到 `/Users/macos/Documents/other_project/bilipai-vr` |
| Meta Spatial SDK | ⬜ 待安装 |
| Meta 开发者应用 | ⬜ 待注册 |

注意：本机还连着一台小米手机（`192.168.41.78:5555`），用 ADB 时**务必指定 `-s 1WMHH86ARM1335`**，否则会因多设备报错。

---

## 1. 前置检查

### 1.1 Quest 固件版本
Spatial SDK 要求 Horizon OS **v69+**。在 Quest 里查看：
设置 → 系统 → 关于 → 系统软件版本。

若低于 v69，先升级固件。

### 1.2 本机工具链
参考官方样例要求（HybridSample README）：

| 工具 | 要求 | 本机 | 备注 |
| --- | --- | --- | --- |
| Android Studio | Narwhal 2025.1.1+ | 需确认 | 命令行构建需 JDK |
| JDK | 17 | 需确认 | BiliPai 用 JDK 21，需确认共存 |
| Gradle | 9.4.1+ | 随项目 wrapper | |
| Android SDK | platform 34+ | 需确认 | |

检查命令：
```bash
java -version
# Android SDK 路径（macOS 默认）
ls ~/Library/Android/sdk
```

### 1.3 Meta 开发者账号与应用
1. 注册开发者：`developers.meta.com`
2. 创建应用，拿 `appId` / `appSecret`（用于 Oculus 登录、后续发布）
3. 把 Quest 设备加入该开发者组织的"测试设备"

---

## 2. 安装 Meta Spatial Editor（可选但推荐）

用于可视化编辑 `.metaspatial` 场景（HybridSample 的场景是 `Composition.metaspatialcomp`）。

- 下载：`developers.meta.com/horizon/downloads/spatial-sdk/`
- macOS 安装后，`build.gradle.kts` 里 `cliPath` 指向：
  ```
  /Applications/Meta Spatial Editor.app/Contents/MacOS/CLI
  ```
- 如果不装，直接用仓库里已有的场景文件也行（HybridSample 自带场景资产）。

---

## 3. 跑通官方 HybridSample（关键里程碑）

> 这一步证明"本机能构建 Spatial SDK 应用 + 能侧载到 Quest + SDK 链路可用"。**在动 BiliPai 之前必须先跑通。**

```bash
# 1. 克隆官方样例
cd /Users/macos/Documents/other_project
gh repo clone meta-quest/Meta-Spatial-SDK-Samples meta-spatial-samples
cd meta-spatial-samples/HybridSample

# 2. 用 Android Studio 打开（或命令行构建）
./gradlew :app:assembleDebug

# 3. 侧载到 Quest
adb -s 1WMHH86ARM1335 install -r app/build/outputs/apk/debug/app-debug.apk

# 4. 启动（进 VR 空间）
adb -s 1WMHH86ARM1335 shell am start -n com.meta.spatial.samples.hybridsample/.HybridSampleActivity

# 5. 验证：Quest 里应出现 3D 环境 + 一块可交互的面板
```

**通过标准**：Quest 里能看到 3D 场景、面板能点击交互、手柄激光/手部追踪可用。

---

## 4. 常见坑与对策

| 坑 | 表现 | 对策 |
| --- | --- | --- |
| ADB 多设备 | `adb: more than one device` | 全程加 `-s 1WMHH86ARM1335` |
| SDK 需要 appId | 构建/运行报缺失 | 在 build.gradle.kts 配置 Meta 应用的 appId/appSecret |
| 固件版本低 | 运行闪退/无 VR 渲染 | 升级 Horizon OS 到 v69+ |
| AGP 版本冲突 | Gradle sync 失败 | quest flavor 可用样例同款 toolchain（AGP 8.11 / Kotlin 2.1 / JDK 17），别动 mobile flavor |
| 场景文件缺失 | 找不到 Environment | 用仓库自带 scenes，或装 Spatial Editor 重新导出 |

---

## 5. 验收清单（Phase 0 完成 = 全部勾选）

- [ ] Quest 固件 ≥ v69
- [ ] Meta 开发者账号 + 应用已注册
- [ ] Android Studio / JDK 17 / Android SDK 就绪
- [ ] HybridSample 在 Quest 上跑通（看到 VR 面板可交互）
- [ ] 确认 SDK 的 appId 配置方式，能复用到 BiliPai
