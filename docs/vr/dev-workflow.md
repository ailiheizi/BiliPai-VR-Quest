# VR 开发与调试工作流

> 目标：把"戴头显→手工点→回报"的慢循环，换成"容器测试拦截布局 bug + 脚本化点击 + 桌面镜像"的快循环。
> 本文基于多 agent 调研（Meta Spatial SDK 0.13.2 官方文档 + HybridSample 源码反编译），是 BiliPai VR 的日常开发手册。

---

## 一、迭代回路（快 → 慢）

```
1. 容器测试（Robolectric，JVM，秒级）   ← 布局溢出/被裁/hover 尺寸，不上机就拦
        │
2. 编译 + 单测（:app:compileQuestDebugKotlin / :app:testQuestDebugUnitTest）
        │
3. 侧载（scripts/vr_sideload.sh 一键装+启）
        │
4. MQDH Cast（可选）桌面镜像 + 鼠标键盘输入转发
        │
5. 真机手感验证（射线/捏合/72fps——只有真机能判）
```

## 二、容器测试（JVM，无需头显）

- 原理：Robolectric 在 JVM 上模拟 Android 运行时 + Compose UI 测试，**真实布局**，断言语义树/边界。
- 固定面板窗口：`@Config(qualifiers = "w560dp-h140dp")` 等，让测试窗口 = 面板注册尺寸。
- 已落地：`app/src/testQuest/java/.../VrControlBarLayoutRobolectricTest.kt`
  - 渲染真实导航条，`getBoundsInRoot` 断言展开态 3 按钮都在面板边界内。
  - 曾拦截的真实 bug：3 按钮（240dp×3）超出 560dp 面板 → 第三个被裁剪。
- 后续候选：播放器底栏（VR 策略大控件是否溢出 1280dp 窗口）、TopControlBar、首页网格。
- 依赖：`org.robolectric:robolectric` + `androidx.compose.ui:ui-test-junit4`（版本对齐 Compose BOM）。
- 注意：Robolectric 不能判射线手感和 72fps——那是真机职责。

## 三、脚本化点击 / 截图（adb broadcast，不碰鼠标）

Spatial SDK 内置未文档化的 `AIDebugToolsFeature`（在 `meta-spatial-sdk-hotreload` AAR 中）：
- 注册：`AIDebugToolsFeature(activity)`（已在 ImmersiveActivity DEBUG 分支注册）。
- 用法（主机侧）：
  ```bash
  adb -s <serial> shell am broadcast -a com.meta.spatial.debugtools.AI_DEBUG_COMMAND \
    -e command inject_panel_click -e entity_name <name> -e pixel_x <x> -e pixel_y <y>
  ```
- 可用命令：`inject_panel_click` / `inject_panel_hover` / `inject_ray_click` / `take_screenshot` /
  `get_panel_ui_elements` / `get_panels` / `get_entity_details` / `find_entity_by_name` /
  `get_last_crash` / `check_anrs` 等（未文档化，API 随 SDK 版本可能变化，用前按你的版本核对）。
- 价值：可写自动化回归（如"返回键到底点不点得到"），替代真人手点。

## 四、桌面镜像 + 输入转发（可选，需 MQDH）

- **Meta Quest Developer Hub（MQDH）**：Devices → Device Actions → Cast = 桌面低延迟镜像 + 截图/录屏。
- **输入转发**（CastInputForwardFeature，已在 DEBUG 分支注册）：仅当你点 Cast 窗口内"输入转发"图标时才接管鼠标键盘（鼠标=激光，LMB=点击，WASD=移动）；鼠标移到别处即释放。不想用就不激活。
- 要求：MQDH ≥5.0；Cast 用 Original(1:1) 比例；debug 构建。
- 顺带：MQDH 的"ADB over Wi-Fi"是 Horizon v81+ 上最稳的无线 ADB 方案（系统配对页已被 Meta 锁掉）。

## 五、帧率 / 性能监控（零依赖）

- 一行日志看帧率与卡顿：
  ```bash
  adb -s <serial> logcat -s VrApi
  ```
  每秒一行：`FPS=72/72, Stale=0, App=4.49ms, CPU&GPU=12.96ms, GPU%=0.43, PLS=0`。
  判读：FPS 达标即满帧；`Stale`>0 = 抖动；`App`>13.88ms(72Hz) = GPU 瓶颈；`PLS`>0 = 过热降频。
- 进阶：OVR Metrics Tool（头显内 HUD/CSV）+ MQDH Performance Analyzer（Perfetto/逐阶段 GPU 时间线）。

## 六、其它有用命令

```bash
# 无线 ADB（MQDH 之外的老办法）
adb shell ip route            # 拿 Quest IP
adb tcpip 5555 && adb connect <ip>:5555

# 模拟戴头显（脚本化测试用）
adb shell am broadcast -a com.oculus.vrpowermanager.prox_close
```

---

## 优先级建议

1. **容器测试覆盖播放器底栏**——VR 大控件溢出 1280dp 窗口是我们踩过的真坑。
2. 跑通 `AIDebugToolsFeature` 脚本化点击回归"返回键"。
3. 需要看画面时再开 MQDH Cast；需要无线时开 MQDH ADB over Wi-Fi。
