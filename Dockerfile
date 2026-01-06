# 第一阶段：构建
# 指定基础镜像，包含 Maven 和 JDK 21，AS build 给这个阶段起个名字，方便后面引用
FROM maven:3.9.6-eclipse-temurin-21 AS build
# 设置容器内的当前工作目录为 /app 。后续的命令都会在这个目录下执行。
WORKDIR /app

# 将宿主机当前目录下的所有文件（源代码、pom.xml 等）拷贝到镜像的 /app 目录中 。
#COPY . .
# 仅拷贝 pom.xml [cite: 1]
COPY pom.xml .
# 预下载依赖 (利用缓存)
# go-offline 命令会下载所有插件和依赖，只要 pom.xml 不变，这一层永远被缓存
RUN mvn dependency:go-offline -B
# 拷贝源码
COPY src ./src
# 在容器内执行 Maven 打包命令 。
RUN mvn clean package -DskipTests

# 第二阶段：运行
# 使用仅包含 JRE（Java 运行时环境）的轻量级镜像，不再需要 Maven 和 JDK 编译器 。
FROM eclipse-temurin:21-jre-jammy
# 再次设置运行环境的工作目录为 /app
WORKDIR /app
# 从构建阶段拷贝 JAR 包
COPY --from=build /app/target/productivity-hub-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口，声明容器在运行时监听 8888 端口 。这通常用于文档说明，实际映射需在 docker run 时指定。
EXPOSE 8888

# 启动命令，通过环境变量指定 Profile
ENTRYPOINT ["java", "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", "-jar", "app.jar"]


# ========================================================================================
# 其他相关命令：
# 构建镜像
# docker build -t productivity-hub .
#
# 首次启动/更新启动，推荐。自动执行编译、打包、创建镜像并以后台模式启动所有容器。
# docker-compose up -d --build
#
# 仅启动，启动已存在的镜像，不会重新编译 Java 代码。
# docker-compose up -d
#
# 停止并删除，停止所有容器并移除相关的网络。
# docker-compose down
#
# 查看实时日志
# docker-compose logs -f app
#
# 查看容器状态，确认所有容器（db, redis, rabbitmq, app）是否都处于 Up 状态。
# docker-compose ps
# ========================================================================================