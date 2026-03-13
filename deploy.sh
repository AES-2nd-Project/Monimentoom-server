ubuntu@ip-172-31-15-62:~/app$ cat /home/ubuntu/app/deploy.sh
#!/bin/bash
set -e

APP_DIR="/home/ubuntu/app"
NGINX_CONF="$APP_DIR/nginx.conf"

run_compose() {
    cd "$APP_DIR" && docker compose "$@"
}

echo "======================================"
echo " Blue-Green 무중단 배포 시작"
echo "======================================"

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

echo "[1/5] 최신 이미지 pull..."
run_compose pull "$NEXT"

echo "[2/5] $NEXT 컨테이너 실행..."
run_compose up -d --no-deps "$NEXT"

echo "[3/5] 헬스체크 대기..."
echo "  앱 초기 기동 대기 (20초)..."
sleep 20

HEALTH_URL="http://localhost:$NEXT_PORT/actuator/health"
STATUS="000"
for i in {1..20}; do
    STATUS=$(curl -s -o /dev/null -w "%{http_code}" "$HEALTH_URL" || true)
    if [ "$STATUS" = "200" ] || [ "$STATUS" = "403" ]; then
        echo "✅ 헬스체크 통과 (${i}회 시도)"
        break
    fi
    echo "  대기 중... ($i/20) status=$STATUS"
    sleep 3
done

if [ "$STATUS" = "200" ] && [ "$STATUS" = "403" ]; then
    echo "❌ 헬스체크 실패 — 롤백"
    run_compose stop "$NEXT"
    exit 1
fi

echo "[4/5] Nginx를 $NEXT 로 전환..."
sed -i "s/server $INACTIVE:8080/server $NEXT:8080/g" "$NGINX_CONF"
docker compose -f "$APP_DIR/docker-compose.yml" restart nginx
echo "✅ Nginx reload 완료"

echo "[5/5] $INACTIVE 컨테이너 종료..."
run_compose stop "$INACTIVE"

docker image prune -f

echo "======================================"
echo "✅ 배포 완료: $NEXT 활성화"
echo "======================================"
