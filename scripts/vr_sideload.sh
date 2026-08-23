#!/usr/bin/env bash
# BiliPai VR (quest flavor) 一键构建 + 侧载 + 启动
# 用法: scripts/vr_sideload.sh [--no-build] [--no-launch]
set -euo pipefail

QUEST_SERIAL="${QUEST_SERIAL:-1WMHH86ARM1335}"
APK_DIR="app/build/outputs/apk/quest/debug"

cd "$(dirname "$0")/.."

if [[ "${1:-}" != "--no-build" ]]; then
  echo "==> 构建 :app:assembleQuestDebug"
  ./gradlew :app:assembleQuestDebug --console=plain -q
fi

APK="$(ls "$APK_DIR"/*.apk 2>/dev/null | head -1)"
if [[ -z "$APK" || ! -f "$APK" ]]; then
  echo "❌ 未找到 APK: $APK_DIR/*.apk（先跑一次构建）"
  exit 1
fi

echo "==> 侧载到 Quest ($QUEST_SERIAL): $APK"
adb -s "$QUEST_SERIAL" install -r "$APK"

if [[ "${@/--no-launch/}" == "$@" ]]; then
  echo "==> 启动 ImmersiveActivity"
  adb -s "$QUEST_SERIAL" shell am force-stop com.android.purebilibili.debug 2>/dev/null || true
  adb -s "$QUEST_SERIAL" shell am start -W \
    -n com.android.purebilibili.debug/com.android.purebilibili.quest.ImmersiveActivity | grep -E "Status" || true
fi

echo "✅ 完成。Quest 里看应用库（未知来源）→ BiliPai Debug"
