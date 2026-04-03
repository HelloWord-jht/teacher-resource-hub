SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS `teacher_resource_hub`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE `teacher_resource_hub`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `delivery_record`;
DROP TABLE IF EXISTS `resource_search_log`;
DROP TABLE IF EXISTS `visit_trace`;
DROP TABLE IF EXISTS `import_task`;
DROP TABLE IF EXISTS `resource_storage`;
DROP TABLE IF EXISTS `content_campaign`;
DROP TABLE IF EXISTS `lead_channel`;
DROP TABLE IF EXISTS `resource_recommend`;
DROP TABLE IF EXISTS `resource_tag`;
DROP TABLE IF EXISTS `resource_preview`;
DROP TABLE IF EXISTS `lead`;
DROP TABLE IF EXISTS `faq`;
DROP TABLE IF EXISTS `site_config`;
DROP TABLE IF EXISTS `page_content`;
DROP TABLE IF EXISTS `resource`;
DROP TABLE IF EXISTS `resource_source`;
DROP TABLE IF EXISTS `tag`;
DROP TABLE IF EXISTS `category`;
DROP TABLE IF EXISTS `admin_user`;

CREATE TABLE `admin_user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录用户名',
  `password` VARCHAR(255) NOT NULL COMMENT 'BCrypt加密密码',
  `nickname` VARCHAR(50) NOT NULL COMMENT '管理员昵称',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `last_login_time` DATETIME NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='后台管理员表';

CREATE TABLE `category` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `code` VARCHAR(10) NOT NULL COMMENT '分类缩写，用于资源码生成',
  `slug` VARCHAR(100) NOT NULL COMMENT '分类别名',
  `description` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '分类简介',
  `icon` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '分类图标URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越大越靠前',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源分类表';

CREATE TABLE `tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '标签名称',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越大越靠前',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源标签表';

CREATE TABLE `resource_source` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `source_name` VARCHAR(100) NOT NULL COMMENT '来源名称',
  `source_type` VARCHAR(30) NOT NULL DEFAULT 'self_compiled' COMMENT '来源类型：self_compiled、cooperation、submission、curated',
  `owner_name` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '来源方或授权方名称',
  `owner_contact` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '来源方联系方式',
  `authorization_proof_url` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '授权证明材料URL',
  `authorization_status` VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '授权审核状态：APPROVED、PENDING、REJECTED、RISK',
  `audit_remark` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '审核备注',
  `auditor_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '审核人',
  `audited_at` DATETIME NULL DEFAULT NULL COMMENT '审核时间',
  `risk_level` VARCHAR(20) NOT NULL DEFAULT 'LOW' COMMENT '风险等级：LOW、MEDIUM、HIGH',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源来源与授权审核表';

CREATE TABLE `resource` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_code` VARCHAR(50) NOT NULL COMMENT '资源码',
  `title` VARCHAR(150) NOT NULL COMMENT '资源标题',
  `slug` VARCHAR(180) NOT NULL COMMENT '资源别名',
  `category_id` BIGINT NOT NULL COMMENT '分类ID',
  `source_id` BIGINT NOT NULL COMMENT '来源ID',
  `summary` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '资源简介',
  `cover_image` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '封面图URL',
  `display_price` DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT '展示价格',
  `grade` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '适用年级',
  `scene` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '适用场景',
  `content_items_json` TEXT NOT NULL COMMENT '资源包含内容清单JSON',
  `description_html` LONGTEXT NOT NULL COMMENT '资源说明HTML',
  `usage_notice` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '使用说明',
  `delivery_notice` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '交付说明',
  `search_keywords` TEXT NOT NULL COMMENT '分词结果与搜索关键词',
  `authorization_status_snapshot` VARCHAR(30) NOT NULL DEFAULT 'PENDING' COMMENT '资源授权状态快照',
  `preview_count` INT NOT NULL DEFAULT 0 COMMENT '预览图数量',
  `content_item_count` INT NOT NULL DEFAULT 0 COMMENT '包含内容数量',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '发布状态：0下线 1已发布',
  `is_recommended` TINYINT NOT NULL DEFAULT 0 COMMENT '是否推荐：0否 1是',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `consult_count` INT NOT NULL DEFAULT 0 COMMENT '咨询量',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越大越靠前',
  `publish_time` DATETIME NULL DEFAULT NULL COMMENT '发布时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源主表';

CREATE TABLE `resource_preview` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `image_url` VARCHAR(255) NOT NULL COMMENT '预览图URL',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越大越靠前',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源预览图表';

CREATE TABLE `resource_tag` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `tag_id` BIGINT NOT NULL COMMENT '标签ID',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源标签关联表';

CREATE TABLE `resource_recommend` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` BIGINT NOT NULL COMMENT '当前资源ID',
  `recommended_resource_id` BIGINT NOT NULL COMMENT '推荐资源ID',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越大越靠前',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源推荐关联表';

CREATE TABLE `faq` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `question` VARCHAR(255) NOT NULL COMMENT '问题',
  `answer` TEXT NOT NULL COMMENT '回答',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，数值越大越靠前',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0禁用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='常见问题表';

CREATE TABLE `lead_channel` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_key` VARCHAR(50) NOT NULL COMMENT '渠道标识，例如 website、xiaohongshu',
  `channel_name` VARCHAR(50) NOT NULL COMMENT '渠道名称',
  `channel_type` VARCHAR(30) NOT NULL DEFAULT 'other' COMMENT '渠道类型：website、xiaohongshu、wechat_official、other',
  `description` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '渠道说明',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='渠道管理表';

CREATE TABLE `content_campaign` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel_id` BIGINT NOT NULL COMMENT '渠道ID',
  `title` VARCHAR(150) NOT NULL COMMENT '投放标题',
  `content_type` VARCHAR(30) NOT NULL COMMENT '内容类型：note、article、landing_page、image_post',
  `publish_time` DATETIME NULL DEFAULT NULL COMMENT '发布时间',
  `target_resource_id` BIGINT NULL DEFAULT NULL COMMENT '目标资源ID',
  `target_resource_code` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '目标资源码',
  `landing_page` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '落地页地址',
  `tracking_code` VARCHAR(100) NOT NULL COMMENT '追踪码',
  `view_count` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `consult_count` INT NOT NULL DEFAULT 0 COMMENT '咨询量',
  `wechat_add_count` INT NOT NULL DEFAULT 0 COMMENT '加微量',
  `deal_count` INT NOT NULL DEFAULT 0 COMMENT '成交量',
  `remark` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '备注',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='内容投放与落地页归因表';

CREATE TABLE `lead` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` VARCHAR(50) NOT NULL COMMENT '老师称呼',
  `contact` VARCHAR(100) NOT NULL COMMENT '联系方式，可填手机号或微信号',
  `source_page` VARCHAR(120) NOT NULL DEFAULT '' COMMENT '来源页面路径',
  `message` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '咨询内容',
  `status` TINYINT NOT NULL DEFAULT 0 COMMENT '跟进状态：0待跟进 1跟进中 2已完成 3无效',
  `follow_up_note` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '跟进备注',
  `channel` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '来源渠道',
  `tracking_code` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '追踪码',
  `target_resource_id` BIGINT NULL DEFAULT NULL COMMENT '目标资源ID',
  `target_resource_code` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '目标资源码',
  `wechat_added_status` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已加微信：0未加 1已加',
  `deal_status` TINYINT NOT NULL DEFAULT 0 COMMENT '成交状态：0未成交 1已成交 2已关闭',
  `last_follow_up_time` DATETIME NULL DEFAULT NULL COMMENT '最后跟进时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='咨询线索表';

CREATE TABLE `resource_storage` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `storage_platform` VARCHAR(50) NOT NULL DEFAULT 'baidu_pan' COMMENT '网盘平台：baidu_pan、quark_pan、aliyun_pan、tianyi_pan',
  `share_url` VARCHAR(500) NOT NULL COMMENT '分享链接',
  `share_code` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '分享码',
  `extract_code` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '提取码',
  `delivery_note` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '交付备注',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0停用 1启用',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源网盘绑定表';

CREATE TABLE `import_task` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `task_name` VARCHAR(100) NOT NULL COMMENT '任务名称',
  `raw_payload` LONGTEXT NOT NULL COMMENT '原始导入JSON',
  `import_status` TINYINT NOT NULL DEFAULT 0 COMMENT '执行状态：0待执行 1成功 2失败',
  `generated_resource_id` BIGINT NULL DEFAULT NULL COMMENT '生成的资源ID',
  `generated_resource_code` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '生成的资源码',
  `recommended_category_id` BIGINT NULL DEFAULT NULL COMMENT '推荐分类ID',
  `recommended_tag_ids_json` TEXT NOT NULL COMMENT '推荐标签ID JSON',
  `execution_result` TEXT NOT NULL COMMENT '执行结果',
  `operator_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '操作人',
  `executed_at` DATETIME NULL DEFAULT NULL COMMENT '执行时间',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='导入任务表';

CREATE TABLE `delivery_record` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` BIGINT NOT NULL COMMENT '资源ID',
  `resource_code` VARCHAR(50) NOT NULL COMMENT '资源码',
  `lead_id` BIGINT NULL DEFAULT NULL COMMENT '线索ID，可空',
  `delivery_channel` VARCHAR(30) NOT NULL DEFAULT 'wechat' COMMENT '发货渠道，当前默认微信',
  `delivery_content_snapshot` LONGTEXT NOT NULL COMMENT '发货内容快照',
  `delivery_remark` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '发货备注',
  `operator_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '发货人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='发货记录表';

CREATE TABLE `resource_search_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `keyword` VARCHAR(150) NOT NULL COMMENT '搜索关键词',
  `search_type` VARCHAR(30) NOT NULL COMMENT '搜索类型：resource_code、title、slug、id、keyword',
  `matched_resource_id` BIGINT NULL DEFAULT NULL COMMENT '命中的资源ID',
  `operator_name` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '操作人',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源搜索日志表';

CREATE TABLE `visit_trace` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `channel` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '来源渠道',
  `tracking_code` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '追踪码',
  `landing_page` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '落地页',
  `target_resource_id` BIGINT NULL DEFAULT NULL COMMENT '目标资源ID',
  `target_resource_code` VARCHAR(50) NOT NULL DEFAULT '' COMMENT '目标资源码',
  `client_id` VARCHAR(100) NOT NULL DEFAULT '' COMMENT '客户端标识',
  `ip` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '访客IP',
  `user_agent` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '用户代理',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='访问归因记录表';

CREATE TABLE `site_config` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `config_key` VARCHAR(100) NOT NULL COMMENT '配置键',
  `config_value` TEXT NOT NULL COMMENT '配置值',
  `config_group` VARCHAR(50) NOT NULL DEFAULT 'site' COMMENT '配置分组，例如 site、home',
  `description` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '配置说明',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='站点配置表';

CREATE TABLE `page_content` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `page_code` VARCHAR(50) NOT NULL COMMENT '页面编码，例如 about、contact',
  `title` VARCHAR(100) NOT NULL COMMENT '页面标题',
  `content_html` LONGTEXT NOT NULL COMMENT '页面HTML内容',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='固定页面内容表';

SET FOREIGN_KEY_CHECKS = 1;
