# BiliPai Project Guide

This repository is a multi-module Android app built with Kotlin, Jetpack Compose, and Gradle Kotlin DSL.
Use this file as the project-specific overlay on top of the global Codex/OMX guidance.

## Project shape

- `app/`: main Android application, feature UI, player, navigation, ViewModels, policies, tests.
- `settings-core/`: reusable settings and store logic shared by app features.
- `network-core/`: network policy and lower-level networking support.
- `baselineprofile/`: macrobenchmark and baseline profile generation for startup and frame timing work.
- `design-system/`: shared Compose UI components, theming, and UI policy tests (e.g. blur policies).
- `plugin-sdk/`: plugin API surface used by the app's third-party plugin system (published via maven-publish).
- `dolby-ffmpeg-decoder/`: media3 FFmpeg decoder extension for Dolby audio in the player.

## Working defaults

- Prefer small, targeted changes over broad rewrites.
- Preserve the existing app visual language unless the task explicitly asks for redesign.
- Reuse the repository's existing `Policy`, `UseCase`, `ViewModel`, and feature package patterns before creating new abstractions.
- Avoid adding business logic to [`MainActivity.kt`](app/src/main/java/com/android/purebilibili/MainActivity.kt) unless the behavior truly belongs to app shell, deep link routing, or top-level playback orchestration.
- Do not add new dependencies unless the user explicitly asks for one.
- Do not run full package, APK packaging, bundle, install, or release-smoke verification paths unless the user explicitly asks for them.
- After each meaningful completed slice, commit and push the changes so progress is easy to roll back and resume.
- Do not add `Co-Authored-By: Cursor`, `Co-Authored-By: Claude`, `Co-Authored-By: Codex`, `Made-with: Cursor`, or similar AI tool attribution to commit messages or PR descriptions.

## Android and Compose conventions

- Prefer state hoisting: screen composables consume immutable UI state and lambda events; do not pass ViewModels deep into leaf composables.
- Keep UI behavior testable in plain Kotlin where possible by extracting layout, visual, or routing decisions into small policy classes.
- Follow existing Material 3 and adaptive layout patterns already used in the app.
- The primary visual language is **Miuix** (`top.yukonga.miuix.kmp` — miuix-ui, miuix-preference, miuix-blur, miuix-squircle, miuix-icons); prefer its components for new UI, keeping Material 3 as the baseline theming layer.
- For UI changes, check dark theme, tablet or large-screen behavior, and minimum 48dp touch targets.
- Avoid expensive work during composition. Use `remember`, `derivedStateOf`, and stable state models where it reduces recomposition churn.
- For animations, haze, blur, player overlay, or scrolling changes, prefer the lightest effect that preserves smoothness on real devices.

## Module boundaries

- Put reusable settings or preference-domain logic in `settings-core` when it is not app-screen-specific.
- Put shared network or fallback behavior in `network-core`, not scattered through feature UI code.
- Keep screen-specific orchestration in feature packages under `app/src/main/java/com/android/purebilibili/feature/...`.
- When a change affects startup, feed rendering, video detail performance, or scroll smoothness, consider whether `baselineprofile/` needs updates or verification.

## UI efficiency workflow

- For UI and UX tasks, inspect the nearest existing screen, component, and policy tests before changing code.
- Prefer updating an existing component or policy over introducing a parallel UI path.
- If the change is primarily visual, add or update focused policy tests first when there is already a nearby test pattern.
- If the change is interaction-heavy, verify both gesture behavior and fallback behavior for narrow screens or disabled states.

## Verification ladder

Pick the smallest command set that proves the change:

### Local packaging policy

- Only build APK artifacts from the `release` or `dev` variants.
- For installable test handoffs, use `:app:assembleDev` by default.
- Do not assemble, package, install, or hand off `debug` or `smooth` variant APKs. Compile/test tasks on the debug variant (`:app:compileDebugKotlin`, `:app:testDebugUnitTest`) remain fine for local verification.

- Default to **one smallest task** that covers the edited code. Do not run broad compilation or a full test suite merely for confidence.
- Targeted unit test for the touched behavior (preferred when one exists):
  `./gradlew :app:testDebugUnitTest --tests '<ExactTestName>'`
- Kotlin/Compose source with no focused test: compile only its owning module (for app code):
  `./gradlew :app:compileDebugKotlin`
- For `design-system`, `settings-core`, `network-core`, `plugin-sdk`, or another library, use that module's `compileDebugKotlin` or a single exact test — never compile `:app` unless the change reaches `:app`.
- Do not run unfiltered `:app:testDebugUnitTest`, `lint`, `check`, or any `assemble*` task unless the user explicitly asks for broader verification.
- Do not run package/build/install tasks (`assemble*`, `bundle*`, `install*`) or APK packaging unless the user explicitly asks for it. Per the packaging policy above, never assemble or install the `debug` or `smooth` variants. Prefer targeted unit tests, strategy/policy tests, `:app:compileDebugKotlin`, lint, or narrow device checks that match the changed surface.

Use extra verification when the task touches these areas:

- App startup, feed scroll, frame pacing, or rendering:
  `./gradlew :baselineprofile:connectedReleaseAndroidTest`
  or project perf scripts in `scripts/`
- Device smoke or release safety:
  `scripts/release_smoke_gate.sh`
- On-device perf snapshots:
  `scripts/mobile_perf_collect.sh`
  or `scripts/tablet_perf_collect.sh`

## Verification hygiene

- Treat Kotlin daemon, incremental compilation backup, temp-file, or configuration-cache infrastructure errors as build-environment failures first, not product regressions.
- If a Gradle/Kotlin task hits daemon or incremental-compilation file-state errors, stop passive polling immediately and switch to a deterministic fallback such as `--no-daemon` or another clean one-shot verification path.
- Do not keep spinning on long terminal polls once the failure mode is clearly infrastructure-related; report that distinction explicitly and choose the next verification step with the lowest ambiguity.

## Fast local verification (reuse Gradle caches)

For ordinary local verification, use the warm Gradle and Kotlin daemons with the repository's enabled configuration/build caches. Do **not** pass `--no-daemon` or `--no-configuration-cache` by default: both discard the fastest repeat-build path. Reserve them for a confirmed daemon, incremental-compilation, or configuration-cache infrastructure failure.

When `./gradlew` fails on Gradle wrapper download (common symptom: SSL / zip download errors), reuse the already-downloaded Gradle distribution instead of waiting on a broken wrapper fetch.

1. Locate the cached Gradle binary (example path for the wrapper-pinned Gradle 9.5.0; the hash dir varies by machine):

```bash
GRADLE_BIN="$HOME/.gradle/wrapper/dists/gradle-9.5.0-bin/bvnork1r7n8i6kp5cnkibsc9q/gradle-9.5.0/bin/gradle"
```

2. Run compile or unit tests directly with that binary. Prefer online mode so dependencies can resolve; add SSL bypass only when your environment requires it:

```bash
GRADLE_OPTS="-Dmaven.wagon.http.ssl.insecure=true -Dmaven.wagon.http.ssl.allowall=true" \
"$GRADLE_BIN" :app:compileDebugKotlin --console=plain
```

3. Fallback flags — use only after an infrastructure failure, never as routine verification:

- `--no-daemon`: avoids stale Kotlin/Gradle daemon state after crashes.
- `--no-configuration-cache`: avoids long silent `Calculating task graph` stalls on some machines.
- `--console=plain`: streams readable incremental output; do not pipe through `tail` while waiting for first compiler output.

4. Targeted verification examples:

```bash
"$GRADLE_BIN" :design-system:testDebugUnitTest --tests 'com.android.purebilibili.core.ui.blur.BlurIntensityVisualPolicyTest'
"$GRADLE_BIN" :app:compileDebugKotlin
```

5. If the cached distribution path differs, list available installs with:

```bash
ls "$HOME/.gradle/wrapper/dists/"
```

Pick the newest `gradle-*-bin/*/gradle-*/bin/gradle` that matches the wrapper version in `gradle/wrapper/gradle-wrapper.properties`.

## ADB and local device flow

- Confirm a device first: `adb devices`
- Install the `dev` variant (the `debug` variant is banned by the packaging policy):
  `./gradlew :app:installDev`
- Launch from shell when needed:
  `adb shell am start -n com.android.purebilibili.dev/com.android.purebilibili.MainActivity`
- If dev test APKs are required, install them explicitly:
  `./gradlew :app:installDevAndroidTest`

## Change-specific expectations

- Settings UI changes should preserve searchability, icon clarity, and tablet layout behavior.
- Video or player UI changes should consider overlay layering, gesture conflicts, lifecycle resume/pause behavior, and PIP or mini-player side effects.
- Home/feed UI changes should consider frame timing, placeholder behavior, and list stability.
- Build-file changes should keep AGP, Kotlin, Compose, and test dependency versions aligned with the current project direction.

## Skills that are especially useful here

- Use `android-native-dev` for Android build, Gradle, Material 3, accessibility, and troubleshooting guidance.
- Use `android-jetpack-compose-expert` for Compose architecture, state management, navigation, and recomposition/performance guidance.
- Use OMX `explore`, `debugger`, `architect`, and `verifier` roles for large Android tasks before making sweeping edits.

## Token-aware OMX defaults

- Do not call or rely on oh-my-codex or OMX workflows, modes, agents, or skills by default in this repository.
- Only use oh-my-codex or OMX when the user explicitly asks for it in the current conversation.
- When multiple valid workflows exist, prefer the lowest-overhead path that still preserves correctness.
- For simple repository questions, local code lookup, and small targeted edits, prefer direct execution or lightweight read-only exploration before broader orchestration.
- Treat `ralph`, `team`, `ultrawork`, `plan`, `deep-interview`, and other multi-phase workflow skills as high-overhead options. Use them only when the user explicitly asks for them or when the task truly needs planning, persistent retries, or parallel lanes.
- Prefer domain-specific helpers such as `android-native-dev`, `android-jetpack-compose-expert`, `debugger`, `explore`, and `verifier` before generic orchestration skills.
- For OMX or skill usage questions, answer directly unless a workflow skill is clearly necessary to complete a concrete configuration or implementation task.

## Quest VR flavor (BiliPai VR — 长期目标：YouTube VR 级舒适手势)

> 独立于手机端的方向。目标：在 Meta Quest 2/3 上用 Meta Spatial SDK 把 BiliPai 渲染成 VR 曲面面板，达到 YouTube VR 级手势舒适度。完整方案见 `docs/vr/`。

### 不变式（VR 方向，任何 agent 都必须遵守）

- **`quest` productFlavor 独立构建，`mobile` flavor 与手机端 UI 零改动**。所有 VR 改动只在 `app/src/quest/...` 与 `app/build.gradle.kts` 的 quest 分支。
- **不重写 UI、不写 OpenXR/C++ 渲染**。只依赖 Meta Spatial SDK 的 `VRFeature` + `ComposeFeature` + `PanelRegistration`，把现有 Compose 页面渲染成面板。
- **不引入新依赖除非用户明确要求**。Spatial SDK 相关依赖只进 `quest` flavor。
- 手势目标 = **YouTube VR 级舒适**，不是"能点就行"。舒适 = 大控件(96px+)、明显 hover/吸附态、避免触屏专属交互、可抓取 3D 滑块。
- 登录、播放、弹幕、搜索等业务逻辑全部复用现有代码，VR 层只做壳与输入适配。
- **Quest 2 手势精度低于 Quest 3**：目标必须更大、吸附更强；手柄+手势双通道并行。

### 技术路线（抄官方，不发明）

- 抄 `Meta-Spatial-SDK-Samples/HybridSample`：`AppSystemActivity` + `registerFeatures()` 注册 `VRFeature`/`ComposeFeature` + `PanelRegistration` 挂现有 Activity。
- 手势管线：面板内 UI 由 ISDK 自动把空间指针/手部射线/Pinch 翻译成标准 Android touch 事件，**面板内的 Compose 不需要自己写手势代码**。
- 显式启用：`SpatialFeature.INTERACTION` + `IsdkSupportingSystems()`（自动配置手/手柄射线）+ 依赖 `meta-spatial-sdk-isdk`。
- 进阶（面板内 UI 到极限后）：3D 环绕视频墙、`Grabbable` + `IsdkGrabConstraints` 做可抓滑块、`hand.getJointPose`/`getPinchStrength` 做微手势。
- 版本基线（官方 HybridSample）：compileSdk 34 / minSdk 34 / targetSdk 34 / JDK 17 / AGP 8.11 / Kotlin 2.1。BiliPai 当前 AGP 9.3.1 / Kotlin 2.4 / JDK 21 / compileSdk 37 —— **quest flavor 如遇版本冲突，可降 toolchain，但 mobile flavor 永不动**。
- Horizon OS 需 v69+。启动类要加 `com.oculus.intent.category.VR` 类别，否则不出现在 Quest 应用库。

### VR 验证路径

- Quest 设备：`adb -s 1WMHH86ARM1335`（本机还连着一台小米，务必 -s 指定）。
- 构建：`:app:assembleQuestDebug`；侧载 `adb -s 1WMHH86ARM1335 install -r app/build/outputs/apk/quest/debug/app-quest-debug.apk`；启动 `adb -s 1WMHH86ARM1335 shell am start -n com.android.purebilibili.quest/.quest.ImmersiveActivity`。
- 一键流程：`scripts/vr_sideload.sh`（构建+安装+启动）。
- 无 Quest 时的替代验证：先跑通官方 HybridSample 到 Quest（Phase 0 门槛），或对照 Wolvic。
- VR 代码只编译 quest 变体：`:app:compileQuestDebugKotlin`（手机端常规验证命令保持 `:app:compileDebugKotlin` 不变）。

### VR 版本库协作方式

- `origin` = 本机 fork `https://github.com/ailiheizi/BiliPai-VR-Quest.git`；VR 工作统一在 `vr/quest` 分支上提交并推送。
- `upstream` = 上游 `https://github.com/jay3-yy/BiliPai.git`；本地 `main` 分支跟踪 `upstream/main`（手机端纯净基线）。
- 手机端上游更新时：切到 `main` → `git pull upstream main` → 需要时 `git rebase origin/vr/quest` 把 VR 分支跟上（VR 改动集中在 `app/src/quest/` 与少量 VR 门控代码，冲突面小）。
- 注意：本机对上游无写权限（403），所有 push 只到 fork。

### VR 阶段定义（每阶段可验收）

- **Phase 0 — 环境**：JDK 21 + SDK platform 37 + Gradle 9.5.0 装齐；BiliPai `:app:compileDebugKotlin` 可过；官方 HybridSample 在 Quest 上跑通（3D 场景 + 可交互面板 + 手部射线/Pinch 可用）。
- **Phase 1 — 最小 demo**：`quest` flavor 建好；BiliPai 首页以 VR 面板出现在 Quest 应用库；能看首页、点进视频、播放有声；手柄 + 手势都能操作。手势 = 系统默认即可。
- **Phase 2 — 舒适手势（重点）**：播放页大控件(≥96px)、主页卡片墙、hover/吸附反馈、扫码登录、面板距离/曲率舒适；手柄+手势双通道可用；长时间不晕。这一阶段是"YouTube VR 级舒适"的主战场，工作量最大，**优先于任何 3D 进阶功能**。
- **Phase 3 — 稳定**：登录会话保持、网络/清晰度/解码回归、72fps、APK 打包流程可复现。

### 优先级与克制

- 顺序永远：Phase 0 → 1 → 2 → 3。Phase 2 的"面板内大控件+hover"优先于任何 3D 环绕/抓取进阶。
- 不要一开始就写 3D 手势代码；面板内 UI 舒适度达标后，再考虑 Grabbable/微手势。
- quest flavor 的改动要能回滚：提交与 push 按"每个可验收切片"做，不要大段堆积。
