# 小学课件教案资源导流站 MVP

## 项目简介

这是一个面向小学老师的课件教案资源导流站 MVP，核心目标是帮助老师在较短时间内找到可直接上课的资料包，并通过微信完成咨询与下单。

项目聚焦以下高频场景：

- 班主任必备
- 家长会专区
- 班会主题
- 公开课精品
- 期中期末复习
- 节日专题

当前版本不做站内支付、不做用户注册登录、不做真实文件下载交付，重点实现：

- 前台资源展示、筛选、详情预览
- 微信咨询弹窗与导流
- 线索提交与后台跟进
- 后台资源、分类、标签、FAQ、首页配置、站点配置管理

## 技术栈

### 后端

- Java 21
- Spring Boot 3.3.5
- Maven
- MyBatis-Plus
- MySQL 8
- Spring Security
- JWT
- springdoc-openapi / Swagger UI

### 前端

- React 18
- Vite 5
- React Router 6
- Axios
- Zustand
- Ant Design 5
- CSS Modules

### 部署

- Docker
- Docker Compose
- Nginx

## 项目结构

```text
teacher-resource-hub/
├── backend/                 # Spring Boot 后端服务
├── frontend/                # React 前端，包含前台站点与后台管理
├── docs/                    # 设计说明与补充文档
├── sql/                     # 建表、索引、种子数据
└── docker-compose.yml       # 本地与服务器统一启动入口
```

## 快速开始

### 1. 推荐方式：Docker Compose 一键启动

项目根目录已经提供完整容器编排文件，推荐直接使用：

```bash
docker compose up -d --build
```

首次启动说明：

- MySQL 容器会自动执行 `sql/` 目录下的初始化脚本，完成建表、索引和种子数据导入。
- 后端镜像会在容器内执行 Maven 打包，首次构建会下载依赖，时间会比后续重建更久一些。
- 前端镜像会先执行 Vite 构建，再由 Nginx 提供静态站点服务，并代理 `/api` 到后端容器。

启动后访问地址：

- 前台首页：[http://localhost:5173](http://localhost:5173)
- 后台登录页：[http://localhost:5173/admin/login](http://localhost:5173/admin/login)
- 后端 Swagger：[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- MySQL 宿主机端口：`3307`

常用命令：

```bash
# 查看容器状态
docker compose ps

# 查看日志
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql

# 停止服务
docker compose down

# 停止服务并删除数据卷
docker compose down -v
```

### 2. 源码方式启动

#### 环境要求

- JDK 21
- Maven 3.9+
- Node.js 18+
- MySQL 8

#### 数据库初始化

先创建 MySQL 数据库 `teacher_resource_hub`，再按顺序执行以下 SQL：

1. `sql/01_schema.sql`
2. `sql/02_indexes.sql`
3. `sql/03_seed.sql`

默认数据库配置在 `backend/src/main/resources/application.yml` 中：

- 数据库：`teacher_resource_hub`
- 地址：`127.0.0.1:3306`
- 用户名：`root`
- 密码：`root`

#### 启动后端

```bash
cd backend
mvn spring-boot:run
```

#### 启动前端

```bash
cd frontend
npm install
npm run dev
```

开发环境下前端默认会把 `/api` 代理到 `http://127.0.0.1:8080`。

## Linux 服务器部署

推荐直接在 Linux 服务器上使用 Docker Compose 部署，最省心，也最接近当前项目的已验证运行方式。

### 服务器环境要求

- Linux 服务器一台
- 已安装 Git
- 已安装 Docker
- 已安装 Docker Compose Plugin
- 建议至少 2 核 CPU、4GB 内存

### 部署步骤

#### 1. 拉取代码

```bash
git clone https://github.com/HelloWord-jht/teacher-resource-hub.git
cd teacher-resource-hub
```

#### 2. 启动服务

```bash
docker compose up -d --build
```

#### 3. 查看服务状态

```bash
docker compose ps
```

#### 4. 查看日志

```bash
docker compose logs -f backend
docker compose logs -f frontend
docker compose logs -f mysql
```

### 数据初始化说明

当前 Compose 配置已经自动挂载 `./sql` 到 MySQL 容器的 `/docker-entrypoint-initdb.d`，因此：

- 第一次启动时会自动执行建表、索引和种子数据初始化
- 如果 MySQL 数据卷已经存在，则不会重复执行初始化脚本

如果你希望在服务器上“重新初始化数据库”，可以执行：

```bash
docker compose down -v
docker compose up -d --build
```

这会删除当前 MySQL 数据卷并重新导入初始数据，请务必确认没有生产数据再执行。

### 服务器默认端口

- 前端：`5173`
- 后端：`8080`
- MySQL：`3307`

如果服务器开启了防火墙，请记得放行前端访问端口。例如使用 `ufw` 时：

```bash
sudo ufw allow 5173/tcp
sudo ufw allow 8080/tcp
```

如果你不希望外部直接访问 MySQL，建议不要放行 `3307`。

### 生产使用建议

- 上线前建议修改 `docker-compose.yml` 中的 MySQL 密码和 JWT 密钥
- 如果使用公网服务器，建议再配一层 Nginx 或反向代理域名
- 如果要长期运行，建议给服务器配置 HTTPS、备份策略和日志轮转

## 默认管理员账号

- 用户名：`admin`
- 密码：`123456`

说明：

- 数据库中保存的是 BCrypt 加密后的密码，不是明文。

## MySQL 初始化内容

数据库设计包含以下核心表：

- `admin_user`
- `category`
- `tag`
- `resource`
- `resource_preview`
- `resource_tag`
- `resource_recommend`
- `faq`
- `lead`
- `site_config`
- `page_content`

种子数据已包含：

- 默认管理员账号
- 6 个中文资源分类
- 12 个中文标签
- 6 条 FAQ
- 13 条可直接展示的资源数据
- 首页配置、站点配置、固定页面内容
- 示例线索数据

数据库设计说明文件：

- `docs/数据库设计说明.md`

## 前后端联调说明

### 前台接口

- `GET /api/web/home`
- `GET /api/web/categories`
- `GET /api/web/tags`
- `GET /api/web/resources`
- `GET /api/web/resources/{id}`
- `POST /api/web/leads`
- `GET /api/web/pages/{pageCode}`

### 后台接口

- `POST /api/admin/auth/login`
- `GET /api/admin/dashboard`
- 分类 CRUD
- 标签 CRUD
- FAQ CRUD
- 资源 CRUD
- 线索分页与状态更新
- 首页配置读取与保存
- 站点配置读取与保存
- 页面内容读取与保存

### 联调要点

- 前端请求统一从 `/api` 发起
- Docker 模式下由前端容器中的 Nginx 代理到后端容器
- 开发模式下由 Vite 代理到 `http://127.0.0.1:8080`
- 后台接口统一通过 JWT 鉴权
- 前台所有页面均匿名可访问
- 首页推荐、FAQ、微信导流配置均从接口读取
- 通用页面内容统一读取 `/api/web/pages/{pageCode}`

## 页面说明

### 前台

- 首页
- 资源列表页
- 资源详情页
- 关于我们
- 联系我们
- 版权与免责声明
- 退款/补发说明

### 后台

- 登录页
- 仪表盘
- 资源管理
- 资源新增/编辑
- 分类管理
- 标签管理
- FAQ 管理
- 线索管理
- 站点配置
- 首页配置
- 页面内容管理

## 已完成的工程实现

### 后端

- 统一返回结构与分页结构
- 全局异常处理
- 参数校验
- CORS
- Swagger / OpenAPI
- Spring Security + JWT 登录校验
- 前后台接口分离
- 资源、分类、标签、FAQ、线索、配置管理

### 前端

- React + Vite 工程搭建
- Axios 请求封装
- Zustand 登录态与微信弹窗状态管理
- 前后台路由拆分
- 后台登录守卫
- 前台 CSS Modules 页面样式
- Ant Design 后台管理界面
- 资源筛选、分页、详情预览、线索提交
- 微信咨询弹窗与固定悬浮按钮
- 路由级懒加载，降低首屏打包体积

### 容器化

- MySQL、后端、前端三服务 Compose 编排
- MySQL 自动执行初始化 SQL
- 前端容器内置 Nginx 代理 `/api`
- 后端支持环境变量覆盖数据库和 JWT 配置

## 关键目录

### 后端

- `backend/src/main/java/com/teacherresourcehub/controller`
- `backend/src/main/java/com/teacherresourcehub/service`
- `backend/src/main/java/com/teacherresourcehub/mapper`
- `backend/src/main/java/com/teacherresourcehub/security`

### 前端

- `frontend/src/api`
- `frontend/src/router`
- `frontend/src/layouts`
- `frontend/src/pages`
- `frontend/src/components`
- `frontend/src/store`

## 验证记录

已完成以下验证：

- 前端构建：

```bash
cd frontend
npm run build
```

- 后端结构性编译校验：

```bash
cd backend
mvn -q -DskipTests -Djava.version=17 compile
```

- 容器模式配置校验：

```bash
docker compose config
```

- 容器模式联调：

```bash
curl http://127.0.0.1:8080/api/web/home
curl "http://127.0.0.1:8080/api/admin/dashboard"
curl http://127.0.0.1:5173/api/web/home
```

说明：

- 前端构建已通过
- 后端结构性编译已通过
- Docker Compose 已完成真实容器启动与接口联调
- 后端容器实际使用 Java 21 运行

## 后续迭代建议

- 接入对象存储，实现后台图片上传而不是手填 URL
- 为资源详情页加入更完整的富文本编辑能力
- 增加资源场景、年级的枚举化配置管理
- 增加后台操作日志，方便追踪配置变更
- 增加基础 SEO 头信息管理和页面级动态 meta 注入
- 增加线索导出能力，便于销售跟进
- 增加资源预览图拖拽排序能力

## TODO

- [ ] 接入真实文件上传能力，当前版本封面图、二维码、预览图均使用 URL 字段占位
- [ ] 为后台资源编辑页增加富文本编辑器，当前版本使用 HTML 字符串输入
- [ ] 增加更细致的后台权限模型，当前版本仅区分匿名前台与管理员后台
- [ ] 为前端增加更细的打包拆分策略，当前虽然已做路由懒加载，但 Ant Design 相关 chunk 仍然较大
- [ ] 增加自动化测试与接口测试
- [ ] 增加生产环境配置文件与更细致的部署说明，当前已提供本地和 Linux Docker Compose 方案
