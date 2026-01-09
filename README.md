# 项目介绍

1、生产力中心（productivity-hub），计划实现不同类型的有价值的功能，以此来学习、巩固技能提高自己，适应不断严峻的工作环境。
2、项目主要聚焦后端技术。


# 项目结构

```
xyz.yuanjin.project.productivity_hub
├── bootstrap                   // 项目启动模块（包含启动类、全局配置）
│   ├── ProductivityHubApplication.java
│   └── config
│       ├── SecurityConfig.java // 解决你提到的 8081 端口重定向问题
│       ├── MyBatisPlusConfig.java
│       └── AsyncThreadConfig.java // Java 21 虚拟线程调度配置
├── common                      // 基础设施：全局通用工具
│   ├── core                    // 统一返回结果 R、状态码、全局异常处理
│   └── util                    // JWT 工具类、Redis 序列化工具
├── api                         // 用户接口层 (Interfaces)
│   ├── controller              // 按照业务模块拆分
│   │   ├── auth                // OAuth2 登录、Token 刷新
│   │   └── task                // 任务增删改查
│   └── dto                     // 输入参数校验 (Validation)
├── application                 // 应用层 (Application) —— 业务编排与中间件触发
│   ├── service                 // 编排领域服务，处理事务、发送 MQ 消息
│   └── executor                // Quartz 定时任务的具体执行逻辑
├── domain                      // 领域层 (Domain) —— 最核心的业务逻辑
│   ├── task                    // 任务领域：实体 (Entity)、领域服务、聚合根
│   ├── user                    // 用户领域：权限判定、身份建模
│   └── event                   // 领域事件：定义任务过期、完成等事件
└── infrastructure              // 基础设施层 (Infrastructure) —— 技术实现
    ├── repository              // MyBatis Plus Mapper 接口与实现
    ├── external                // 第三方服务调用 (OAuth2 Client)
    └── messaging               // RabbitMQ 生产者与消费者实现
```