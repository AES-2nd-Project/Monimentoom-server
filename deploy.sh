#!/bin/bash
set -e

APP_DIR="/home/ubuntu/app"
NGINX_CONF="$APP_DIR/nginx.conf"

# 💡 변수 대신 함수로 선언하면 명령어 인자 꼬임 문제가 완벽히 해결됩니다.
run_compose() {
    docker compose -f "$APP_DIR/docker-compose.yml" "$@"
}

echo "======================================"
echo " Blue-Green 무중단 배포 시작"
echo "======================================"

# ── 1. 현재 활성 컨테이너 확인 ──────────────
# 최초 배포 시 빈 값일 경우를 대비해 에러 없이 넘어가도록 처리(2>/dev/null)
CURRENT=$(grep "server app-" "$NGINX_CONF" 2>/dev/null | grep -oE "app-(blue|green)" || echo "")

if [ "$CURRENT" = "app-blue" ]; then
    NEXT="app-green"
    NEXT_PORT="8081"
    INACTIVE="app-blue"
else
    NEXT="app-blue"
    NEXT_PORT="8080"
    INACTIVE="app-green"
fi
echo "▶ 현재 활성: ${CURRENT:-없음(최초 배포)}"
echo "▶ 배포 대상: $NEXT"

# ── 2. 최신 이미지 pull ──────────────────────
echo "[1/5] 최신 이미지 pull..."
run_compose pull "$NEXT"

# ── 3. 비활성 컨테이너 새 이미지로 실행 ────────
echo "[2/5] $NEXT 컨테이너 실행..."
run_compose up -d --no-deps "$NEXT"

# ── 4. 헬스체크 ─────────────────────────────
echo "[3/5] 헬스체크 대기..."
HEALTH_URL="http://localhost:$NEXT_PORT/actuator/health"
for i in {1..20}; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" || true)
    if [ "$STATUS" = "200" ]; then
        echo "✅ 헬스체크 통과 (${i}회 시도)"
        break
    fi
    echo "  대기 중... ($i/20) status=$STATUS"
    sleep 3
done