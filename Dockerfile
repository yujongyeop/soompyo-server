# syntax=docker/dockerfile:1

########################
# 1) Builder stage
########################
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /workspace

# Gradle 래퍼/의존성 캐시 최적화
COPY gradlew ./gradlew
COPY gradle  ./gradle
COPY build.gradle settings.gradle ./

# (윈도우 체크아웃 대비) 줄바꿈/권한 수정
RUN sed -i 's/\r$//' gradlew && chmod +x gradlew

# 의존성만 먼저 내려받아 캐시층 생성
RUN ./gradlew dependencies --no-daemon || true

# 소스 복사 후 빌드
COPY src ./src
RUN ./gradlew clean bootJar -x test --no-daemon

########################
# 2) Runtime stage
########################
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌드 산출물 복사
COPY --from=builder /workspace/build/libs/*.jar app.jar

# 컨테이너 환경 최적화 JVM 옵션
ENV JAVA_TOOL_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]