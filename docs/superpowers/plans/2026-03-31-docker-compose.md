# Docker Compose 封装 Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为现有 monorepo 补齐 Docker Compose 一键启动环境，并在容器启动后完成真实联调验证。

**Architecture:** 使用 `mysql + backend + frontend` 三服务结构。数据库通过挂载 SQL 自动初始化，后端通过环境变量覆盖连接配置，前端使用 Nginx 托管打包产物并反向代理 `/api` 到后端。

**Tech Stack:** Docker Compose、MySQL 8、Spring Boot 3、Java 21、React 18、Vite、Nginx

---

### Task 1: 补齐后端容器入口

**Files:**
- Create: `/Users/jht/Documents/Playground/teacher-resource-hub/backend/Dockerfile`
- Create: `/Users/jht/Documents/Playground/teacher-resource-hub/backend/.dockerignore`
- Modify: `/Users/jht/Documents/Playground/teacher-resource-hub/backend/src/main/resources/application.yml`

- [ ] 增加后端多阶段构建 Dockerfile
- [ ] 增加后端 .dockerignore，排除 target 等无关内容
- [ ] 将后端 datasource 与 jwt 配置改为支持环境变量覆盖

### Task 2: 补齐前端容器入口

**Files:**
- Create: `/Users/jht/Documents/Playground/teacher-resource-hub/frontend/Dockerfile`
- Create: `/Users/jht/Documents/Playground/teacher-resource-hub/frontend/nginx.conf`
- Create: `/Users/jht/Documents/Playground/teacher-resource-hub/frontend/.dockerignore`

- [ ] 增加前端多阶段构建 Dockerfile
- [ ] 使用 Nginx 托管 dist 目录
- [ ] 配置 `/api` 反向代理和 SPA 路由回退

### Task 3: 组装 Compose 环境

**Files:**
- Create: `/Users/jht/Documents/Playground/teacher-resource-hub/docker-compose.yml`

- [ ] 定义 mysql、backend、frontend 三个服务
- [ ] 为 MySQL 配置初始化 SQL 挂载和健康检查
- [ ] 为 backend 配置数据库环境变量和依赖顺序
- [ ] 为 frontend 配置端口映射和 backend 依赖

### Task 4: 启动与验证

**Files:**
- Modify: `/Users/jht/Documents/Playground/teacher-resource-hub/README.md`

- [ ] 执行 `docker compose down -v`
- [ ] 执行 `docker compose up --build -d`
- [ ] 检查 `docker compose ps`
- [ ] 请求首页接口、资源接口、后台登录接口、页面内容接口
- [ ] 请求前端首页 HTML 验证 Nginx 站点
- [ ] 更新 README 的 Docker 使用说明与端口说明
