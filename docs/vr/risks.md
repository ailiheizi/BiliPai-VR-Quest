# 风险与验证清单

> 做 BiliPai VR 之前必须知道的风险，以及每阶段的可验证标准。

---

## 一、技术风险清单

| # | 风险 | 概率 | 影响 | 对策 |
| --- | --- | --- | --- | --- |
| 1 | **AGP/Kotlin 版本冲突**：BiliPai 用 AGP 9.3.1 + Kotlin 2.4，Spatial SDK 官方样例用 AGP 8.11 + Kotlin 2.1 | 中 | 高（构建失败） | 先跑通 HybridSample；quest flavor 可降 toolchain，mobile 不动 |
| 2 | **Spatial SDK 依赖在 AGP 9 上不可用** | 中 | 高 | 若如此，quest flavor 用独立 wrapper / 降级；或等 SDK 支持新 AGP |
| 3 | **Compose BOM 版本不匹配**：BiliPai 用 Compose BOM 2026.06，SDK 样例较老 | 中 | 中 | Spatial SDK 的 compose 模块对 BOM 版本敏感，可能要 pin 版本 |
| 4 | **Quest 2 手势精度差**（无红外深度相机，比 Quest 3 差） | 高（Quest 2） | 中 | 目标放大、强吸附；保留手柄双通道 |
| 5 | **Horizon OS 版本 < v69** | 低（已确认 Quest 2 可升） | 高 | 升级固件 |
| 6 | **登录会话丢失**（你最早的问题） | 中 | 中 | 扫码登录 + 会话本地持久化；quest flavor 注意 Cookie 保存路径 |
| 7 | **弹幕/视频在曲面面板渲染异常** | 中 | 中 | 用 MediaPlayerSample 验证播放链路；弹幕可先面板内 |
| 8 | **Media3 播放器在 VR 面板的输出** | 中 | 中 | 官方 MediaPlayerSample 已证明可行，照抄 |
| 9 | **Quest 2 性能/发热**（72fps 渲染 + 视频解码） | 中 | 中 | 硬件解码（media3 默认）、限制面板数量 |
| 10 | **上游 BiliPai 更新冲突** | 低 | 低 | fork 后定期 rebase；quest 改动集中在独立文件 |

---

## 二、决策风险

| 决策 | 风险 | 备注 |
| --- | --- | --- |
| 用 BiliPai 而非 PiliPlus | 若 Spatial SDK 与 BiliPai 版本冲突无法解决 | 备选：等 SDK 更新，或改走 Wolvic + 网页 |
| Fork 而非 PR | 无法获得上游维护者帮助 | 但保住手机端纯净；Quest 版独立演进 |
| 用 Spatial SDK 而非自写 OpenXR | SDK 是官方方案，风险低 | 自写需要 C++/重写 UI，工作量 10x |
| 兜底方案 Wolvic | 已装好，可随时对照/回退 | 不花钱不开发 |

---

## 三、阶段验证清单

### Phase 0（环境）
- [ ] HybridSample 在 Quest 上显示 3D 场景 + 可交互面板
- [ ] 手柄激光可点、手势可捏合

### Phase 1（demo）
- [ ] BiliPai 以 VR 面板形态出现在 Quest 应用库
- [ ] 能看首页、点进视频、播放有声音
- [ ] 手柄 + 手势都能操作

### Phase 2（交互）
- [ ] 播放页大控件全部可点可拖
- [ ] 主页大卡片墙可浏览
- [ ] 登录用扫码，不输文字
- [ ] 面板距离/曲率舒适，长时间不晕

### Phase 3（稳定）
- [ ] 登录会话保持（重启后仍在）
- [ ] 网络切换/弱网可恢复
- [ ] 72fps 稳定、不发热掉帧
- [ ] APK 打包流程可复现

---

## 四、"值不值得做"的决策点

**做之前先问自己**：Wolvic + B站网页版，是否已经 80% 满足需求？
- 如果网页版体验可接受 → **别写代码，直接用 Wolvic**
- 如果网页版在"播放控制/手势/输入"上确实达不到 → **投入做 BiliPai VR**（2-3 周 demo）

**Phase 1 demo 跑通后**是第二次决策点：
- 体验好 → 继续 Phase 2/3
- 体验一般 → 及时止损，回 Wolvic，把 Phase 1 当技术验证

---

## 五、当前进度

- [x] 调研完成（4 个 agent，结论见 `references.md`）
- [x] 决策：BiliPai + Meta Spatial SDK + Fork，不用 PiliPlus/插件系统/自写 OpenXR
- [x] BiliPai 已克隆到 `/Users/macos/Documents/other_project/bilipai-vr`
- [x] Wolvic 已侧载到 Quest 2 并实测（作为对照基准）
- [ ] Phase 0：环境搭建 + HybridSample 跑通
- [ ] Phase 1：quest flavor + 最小 demo
- [ ] Phase 2：交互打磨
- [ ] Phase 3：稳定发布
