# 第一阶段：构建
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 第二阶段：运行
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# 从构建阶段拷贝 JAR 包
COPY --from=build /app/target/productivity-hub-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动命令，通过环境变量指定 Profile
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]