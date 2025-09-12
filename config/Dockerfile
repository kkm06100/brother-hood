# 1. 빌드 단계 (Gradle 기준, Maven이면 mvn으로 바꿔라)
FROM gradle:8.8-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# 2. 런타임 단계 (슬림 JDK 이미지 사용)
FROM eclipse-temurin:17-jdk-jammy AS runtime
WORKDIR /app

# 빌드 결과 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 프로세스 실행 시 root 권한 안 쓰게 하기
RUN addgroup --system spring && adduser --system spring --ingroup spring
USER spring:spring

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# Config Server 기본 포트
EXPOSE 8888
