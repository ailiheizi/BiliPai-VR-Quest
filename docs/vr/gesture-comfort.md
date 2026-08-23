# VR 手势舒适度设计（Phase 2 主战场）

> 目标：让 BiliPai 在 Quest 上的手势达到 YouTube VR 级舒适。
> 舒适 = 手部射线/手柄激光能轻松瞄准、hover 有明确反馈、不用精细点击、长时间不晕。
> 前置：`ux-design.md` 定义了交互范式；本文是工程落地规范。

---

## 一、手势舒适的分层模型

| 层 | 内容 | 工作量 | 谁负责 |
| --- | --- | --- | --- |
| L0 系统手势 | 手部射线 + Pinch = 点击，手柄激光 + 扳机 = 点击，由 ISDK 自动翻译成 Android touch | 零代码（只启 feature） | Horizon OS |
| L1 显式启用 | `SpatialFeature.INTERACTION` + `IsdkSupportingSystems()` + `isdk` 依赖 | 少量 | 我们 |
| L2 面板内舒适 | 大控件 / hover 态 / 吸附 / 面板尺寸曲率 / 避免触屏交互 | **主战场** | 我们（Compose） |
| L3 3D 进阶 | 环绕视频墙、Grabbable 可抓滑块、微手势、面板外 3D 元素 | 进阶，Phase 2 后 | 我们（可选） |

**优先级：L2 永远先于 L3。** L2 做完即可达成"YouTube VR 级舒适"的 90%。

---

## 二、L2 面板内舒适 — 落地规则

### 2.1 尺寸与间距

| 元素 | 规则 |
| --- | --- |
| 可点击按钮/卡片 | 视觉高度 ≥ 96px，宽 ≥ 144px（约 3:2），间距 ≥ 48px |
| 滑块（进度/音量/清晰度） | 高度 ≥ 48px，拖动区域 ≥ 24px 高，thumb ≥ 64px |
| 文字 | 正文 ≥ 24px，最小 18px |
| 导航项 | 侧边/顶部大图标，高度 ≥ 96px，不依赖底部密集小图标 |
| 触摸目标 | ISDK 建议 ray 目标 ≥ 2cm 面板物理尺寸；面板 1.5-2.5m 外时按视角角分辨率换算 |

> 注：96px 是文档的"约 2 倍手机"。Quest 2 目标要比 Quest 3 再大 ~25%，吸附再强一档。

### 2.2 hover / 吸附 / 选中态（核心）

手部射线指向即 hover（系统给 touch hover 事件，Compose 用 `interactionSource`/`hoverable` 感知）：

- **hover 进入**：目标放大 1.05-1.1x + 高亮 + 轻微音效（可选触感）
- **吸附**：射线进入目标 30-50px 邻域即触发放大高亮，降低瞄准难度
- **按下/选中**：明显按压/变色，释放前可取消（手指移开回退）
- **滚动/翻页**：轨道式/翻页式大按钮，避免细滚动条

### 2.3 避免触屏专属交互

| 触屏交互 | VR 替代 |
| --- | --- |
| 滑动列表 | 翻页式/大按钮翻页、摇杆滚动（手柄） |
| 拖动滑块 | 大滑块 + 点按定位 + 可拖区域放大；进阶用 3D Grabbable 滑块 |
| 文本输入 | 扫码登录、VR 系统键盘、语音搜索 |
| 长按菜单 | 直接大按钮 + 明确层级 |

### 2.4 面板参数（`PanelRegistration`）

- 距离：1.5-2.5m，可调
- 曲率：轻微内凹（电影院感），不要太弯
- 锚定：`ReferenceSpace.LOCAL_FLOOR`，固定面板，避免 UI 随头动
- 尺寸：按内容与目标尺寸换算（`layoutWidthInDp`/`layoutHeightInDp`）

---

## 三、L3 3D 进阶（Phase 2 之后，可选）

- **环绕视频墙**：多个 `PanelRegistration` + `Transform` 摆一圈，转头/手柄选择，Pinch 确定
- **可抓 3D 滑块**：进度/音量用 `Grabbable` + `IsdkGrabConstraints`（锁定旋转、只沿 X 轴），手指捏住拖
- **微手势**：`hand.getJointPose`/`getPinchStrength` 做 Point/Fist/OpenHand 快捷动作
- **弹幕/评论**：面板外浮动

> 纪律：L2 达标前不碰 L3。

---

## 四、验收清单（Phase 2 完成 = 全勾选）

- [ ] 播放页控制大按钮全部可点（手部射线 + Pinch）
- [ ] 进度条可拖（点按定位 + 大滑块），音量/清晰度/倍速大控件可点
- [ ] 主页卡片墙 hover 放大高亮、翻页式浏览
- [ ] 搜索能用 VR 键盘输入或扫码，不用触屏键盘
- [ ] 登录扫码，不输文字
- [ ] 手柄 + 手势双通道都能完整操作
- [ ] 面板距离/曲率舒适，长时间不晕（Quest 2 上验证）
- [ ] Quest 2 与 Quest 3 都可舒适操作（Q2 目标更大、吸附更强）

---

## 五、工程落地位置

- 面板注册/场景：`app/src/quest/java/com/android/purebilibili/quest/`
- VR 化 UI：复用现有 feature 的 composable，新增 `quest` sourceSet 下的覆盖或传入"VR 模式"参数
- 尺寸/hover 反馈：集中在 `design-system` 或 quest 专属组件，便于统一调参
- 不改动：`app/src/main/` 的手机 UI 与业务逻辑
