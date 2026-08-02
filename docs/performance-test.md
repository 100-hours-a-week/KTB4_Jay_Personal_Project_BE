# 성능 테스트 명령어

## 서버 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=mysql'
```
## seed 데이터 생성하면서 서버 실행
```bash
./gradlew bootRun --args='--spring.profiles.active=mysql,seed'
```
## 주간 인기글 API 시간 측정
```bash
curl -w "\nTotal: %{time_total}s\n" -o /dev/null -s "http://localhost:8080/posts/rank?period=WEEKLY&size=5"
```

## 기존 쿼리 EXPLAIN
```bash
mysql -uroot --vertical < explain-popular.sql
```
## 개선 쿼리 EXPLAIN
```bash
mysql -uroot --vertical < explain-popular-optimized.sql
```
## 개선 쿼리 EXPLAIN
```bash
mysql -uroot --vertical < explain-popular-batch.sql
```

## 테스트 
```bash
./gradlew test --tests kr.adapterz.springboot.post.PostServiceIntegrationTest jacocoTestReport
open build/reports/jacoco/test/html/index.html
```
## 실행
```bash
./run-local.sh
```