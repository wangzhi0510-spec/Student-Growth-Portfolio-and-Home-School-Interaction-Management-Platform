# 学生成长档案与家校互动管理平台

## 项目简介

本项目是一个基于 Spring Boot + Vue 3 的学生成长档案与家校互动管理平台，实现了学生、教师、家长及管理员等多角色的统一管理。

## 技术栈

### 后端
- Spring Boot 3.2.0
- Spring Security + JWT
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Lombok
- Hutool

### 前端
- Vue 3.4
- Vite 5.2
- Element Plus 2.6
- Vue Router 4.3
- Pinia 2.1
- Axios 1.6
- ECharts 5.5

## 功能模块

### 1. 用户认证与权限控制
- 用户注册与登录
- JWT Token 认证
- 基于角色的权限控制（RBAC）
- 支持管理员、教师、学生、家长四种角色

### 2. 学生基本信息管理
- 学生信息的录入、修改与查询
- 学生列表展示与分页
- 学生信息搜索

### 3. 学生成长档案管理
- **成绩记录管理**：记录学生各科成绩、考试类型、考试日期等
- **综合素质评价**：德育、智育、体育、美育、劳育五育评价
- **教师评语**：日常评语、月度评语、学期评语
- **获奖记录**：记录学生获得的各类奖项

### 4. 家校互动模块
- **消息系统**：教师与家长之间的私信交流
- **通知公告**：学校通知、班级通知、个人通知的发布与管理
- 消息已读/未读状态管理

### 5. 数据统计与展示
- 学生成绩统计（平均分、最高分、最低分）
- 成绩趋势图表展示
- 使用 ECharts 进行数据可视化

### 6. 系统管理模块
- 用户管理：增删改查用户信息
- 班级管理：班级信息的维护
- 角色权限管理

## 项目结构

```
bs/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/student/growth/
│   │   │   │   ├── common/      # 通用类
│   │   │   │   ├── config/      # 配置类
│   │   │   │   ├── controller/  # 控制器
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── entity/      # 实体类
│   │   │   │   ├── filter/      # 过滤器
│   │   │   │   ├── mapper/      # 数据访问层
│   │   │   │   ├── service/     # 业务逻辑层
│   │   │   │   └── util/        # 工具类
│   │   │   └── resources/
│   │   │       ├── application.yml
│   │   │       └── schema.sql
│   │   └── test/
│   └── pom.xml
└── frontend/                   # 前端项目
    ├── src/
    │   ├── api/                 # API 接口
    │   ├── layout/             # 布局组件
    │   ├── router/              # 路由配置
    │   ├── stores/              # 状态管理
    │   ├── styles/              # 样式文件
    │   ├── utils/               # 工具函数
    │   ├── views/               # 页面组件
    │   │   ├── student/         # 学生管理
    │   │   ├── growth/          # 成长档案
    │   │   ├── message/         # 家校互动
    │   │   ├── statistics/      # 数据统计
    │   │   ├── notice/          # 通知公告
    │   │   ├── user/            # 用户管理
    │   │   └── class/           # 班级管理
    │   ├── App.vue
    │   └── main.js
    ├── index.html
    ├── package.json
    └── vite.config.js
```

## 数据库设计

### 主要数据表

1. **sys_user** - 用户表
2. **student_info** - 学生信息表
3. **class_info** - 班级表
4. **score_record** - 成绩记录表
5. **comprehensive_evaluation** - 综合素质评价表
6. **teacher_comment** - 教师评语表
7. **award_record** - 获奖记录表
8. **message** - 消息表
9. **notice** - 通知公告表
10. **notice_read** - 通知阅读记录表
11. **sys_role** - 角色权限表

## 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.6+

### 后端启动

1. 创建数据库并执行初始化脚本
```sql
source backend/src/main/resources/schema.sql
```

2. 修改数据库配置
编辑 `backend/src/main/resources/application.yml`，修改数据库连接信息

3. 启动后端服务
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 `http://localhost:8080/api` 启动

### 前端启动

1. 安装依赖
```bash
cd frontend
npm install
```

2. 启动开发服务器
```bash
npm run dev
```

前端服务将在 `http://localhost:5173` 启动

### 默认账号

- 管理员账号：`admin` / `admin` (密码已加密，实际使用时请修改)

## 接口文档

### 认证接口
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- GET `/api/auth/current` - 获取当前用户信息

### 学生管理
- GET `/api/student/page` - 分页查询学生列表
- GET `/api/student/{id}` - 获取学生详情
- POST `/api/student` - 新增学生
- PUT `/api/student` - 更新学生信息
- DELETE `/api/student/{id}` - 删除学生

### 成绩管理
- GET `/api/score/student/{studentId}` - 获取学生成绩列表
- POST `/api/score` - 新增成绩
- PUT `/api/score` - 更新成绩
- DELETE `/api/score/{id}` - 删除成绩

### 综合素质评价
- GET `/api/evaluation/student/{studentId}` - 获取学生评价列表
- POST `/api/evaluation` - 新增评价
- PUT `/api/evaluation` - 更新评价
- DELETE `/api/evaluation/{id}` - 删除评价

### 教师评语
- GET `/api/comment/student/{studentId}` - 获取学生评语列表
- POST `/api/comment` - 新增评语
- PUT `/api/comment` - 更新评语
- DELETE `/api/comment/{id}` - 删除评语

### 获奖记录
- GET `/api/award/student/{studentId}` - 获取学生获奖列表
- POST `/api/award` - 新增获奖
- PUT `/api/award` - 更新获奖
- DELETE `/api/award/{id}` - 删除获奖

### 消息管理
- GET `/api/message/received` - 获取收到的消息
- GET `/api/message/sent` - 获取发送的消息
- GET `/api/message/unread-count` - 获取未读消息数量
- POST `/api/message` - 发送消息
- PUT `/api/message/{id}/read` - 标记消息已读
- DELETE `/api/message/{id}` - 删除消息

### 通知公告
- GET `/api/notice/page` - 分页查询通知列表
- GET `/api/notice/{id}` - 获取通知详情
- POST `/api/notice` - 发布通知
- PUT `/api/notice` - 更新通知
- DELETE `/api/notice/{id}` - 删除通知

### 数据统计
- GET `/api/statistics/score/{studentId}` - 获取学生成绩统计
- GET `/api/statistics/score/trend/{studentId}` - 获取学生成绩趋势

### 用户管理
- GET `/api/user/page` - 分页查询用户列表
- GET `/api/user/{id}` - 获取用户详情
- POST `/api/user` - 新增用户
- PUT `/api/user` - 更新用户
- DELETE `/api/user/{id}` - 删除用户

### 班级管理
- GET `/api/class/list` - 获取班级列表
- GET `/api/class/{id}` - 获取班级详情
- POST `/api/class` - 新增班级
- PUT `/api/class` - 更新班级
- DELETE `/api/class/{id}` - 删除班级

## 界面预览

### 登录页面
简洁大气的登录界面，支持用户名密码登录

### 首页
展示系统概况、快速入口和最新通知

### 学生管理
学生信息的增删改查，支持搜索和分页

### 成长档案
包含成绩记录、综合素质评价、教师评语、获奖记录四个标签页

### 家校互动
消息列表和消息详情，支持发送和接收消息

### 数据统计
使用 ECharts 展示学生成绩统计和趋势

### 系统管理
用户管理和班级管理（仅管理员可见）

## 开发说明

### 后端开发规范
- 遵循 RESTful API 设计规范
- 统一返回格式：`{ code, message, data }`
- 使用 MyBatis-Plus 简化数据库操作
- 使用 JWT 进行身份认证
- 使用 Spring Security 进行权限控制

### 前端开发规范
- 使用 Vue 3 Composition API
- 使用 Element Plus 组件库
- 使用 Pinia 进行状态管理
- 使用 Vue Router 进行路由管理
- 统一使用 axios 进行 HTTP 请求

## 注意事项

1. 首次运行前请确保数据库已创建并执行了初始化脚本
2. 请根据实际情况修改数据库连接配置
3. 默认管理员密码为 `admin`，生产环境请务必修改
4. 前端开发服务器已配置代理，无需额外配置跨域

## 许可证

本项目仅供学习交流使用。

## 作者

学生成长档案与家校互动管理平台 - 毕业设计项目
