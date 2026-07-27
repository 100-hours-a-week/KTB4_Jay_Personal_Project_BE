# =========================================================
# 1단계: Spring Boot 소스코드를 JAR 파일로 만드는 빌드 단계
# =========================================================

# Java 21 JDK가 들어 있는 이미지를 빌드 환경으로 사용한다.
# JDK는 소스코드를 컴파일할 수 있는 개발 도구까지 포함한다.
FROM eclipse-temurin:21-jdk AS builder

# 이후 명령을 실행할 이미지 내부 기본 위치를 /app으로 지정한다.
WORKDIR /app

# Gradle Wrapper 실행 파일을 이미지 안으로 복사한다.
COPY gradlew ./

# Gradle Wrapper가 사용할 설정과 실행 파일을 복사한다.
COPY gradle ./gradle

# 프로젝트 빌드 설정 파일을 복사한다.
COPY build.gradle settings.gradle ./

# Linux 안에서 gradlew를 실행할 수 있도록 실행 권한을 부여한다.
RUN chmod +x gradlew

# 실제 Java 소스코드를 이미지 안으로 복사한다.
COPY src ./src

# Docker 이미지 빌드 중 Spring Boot 프로젝트를 JAR로 빌드한다.
RUN ./gradlew clean bootJar --no-daemon


# =========================================================
# 2단계: 완성된 JAR 파일만 실행하는 운영 단계
# =========================================================

# 실행만 하면 되므로 JDK 전체가 아니라 JRE 이미지를 사용한다.
FROM eclipse-temurin:21-jre

# 컨테이너 내부 실행 위치를 /app으로 지정한다.
WORKDIR /app

# builder 단계에서 만든 JAR 파일만 현재 이미지로 가져온다.
COPY --from=builder /app/build/libs/*.jar app.jar

# 이 컨테이너가 8080 포트를 사용한다는 의도를 기록한다.
# 실제 외부 포트를 여는 설정은 아니다.
EXPOSE 8080

# 컨테이너가 시작될 때 Spring Boot JAR를 실행한다.
ENTRYPOINT ["java", "-jar", "app.jar"]