SET NAMES utf8mb4;
USE `teacher_resource_hub`;

ALTER TABLE `resource`
  ADD COLUMN `primary_file_id` BIGINT NULL DEFAULT NULL COMMENT '主展示文件ID' AFTER `source_id`,
  ADD COLUMN `preview_available_status` VARCHAR(20) NOT NULL DEFAULT 'none' COMMENT '资源预览可用状态：none、partial、available' AFTER `preview_count`,
  ADD COLUMN `preview_mode` VARCHAR(20) NOT NULL DEFAULT 'single' COMMENT '预览模式：single、multi、zip_bundle' AFTER `preview_available_status`;

ALTER TABLE `import_task`
  MODIFY COLUMN `import_status` VARCHAR(30) NOT NULL DEFAULT 'pending' COMMENT '导入任务状态：pending、processing、partial_success、success、failed',
  ADD COLUMN `import_type` VARCHAR(30) NOT NULL DEFAULT 'json_payload' COMMENT '导入类型：json_payload、resource_upload、zip_upload、mixed_upload' AFTER `task_name`,
  ADD COLUMN `total_file_count` INT NOT NULL DEFAULT 0 COMMENT '文件总数' AFTER `generated_resource_code`,
  ADD COLUMN `recognized_file_count` INT NOT NULL DEFAULT 0 COMMENT '已识别文件数' AFTER `total_file_count`,
  ADD COLUMN `processed_file_count` INT NOT NULL DEFAULT 0 COMMENT '已处理文件数' AFTER `recognized_file_count`,
  ADD COLUMN `preview_success_count` INT NOT NULL DEFAULT 0 COMMENT '预览生成成功文件数' AFTER `processed_file_count`,
  ADD COLUMN `preview_failed_count` INT NOT NULL DEFAULT 0 COMMENT '预览生成失败文件数' AFTER `preview_success_count`,
  ADD COLUMN `unsupported_file_count` INT NOT NULL DEFAULT 0 COMMENT '不可预览文件数' AFTER `preview_failed_count`,
  ADD COLUMN `unzip_status` VARCHAR(30) NOT NULL DEFAULT 'not_applicable' COMMENT '解压状态：not_applicable、pending、processing、success、partial_success、failed' AFTER `unsupported_file_count`,
  ADD COLUMN `preview_status_summary` VARCHAR(1000) NOT NULL DEFAULT '{\"pending\":0,\"processing\":0,\"success\":0,\"failed\":0,\"unsupported\":0}' COMMENT '预览状态汇总JSON字符串' AFTER `unzip_status`;

UPDATE `import_task`
SET `import_status` = CASE `import_status`
  WHEN '0' THEN 'pending'
  WHEN '1' THEN 'success'
  WHEN '2' THEN 'failed'
  WHEN 'pending' THEN 'pending'
  WHEN 'processing' THEN 'processing'
  WHEN 'partial_success' THEN 'partial_success'
  WHEN 'success' THEN 'success'
  WHEN 'failed' THEN 'failed'
  ELSE 'pending'
END;

CREATE TABLE IF NOT EXISTS `resource_file` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_id` BIGINT NULL DEFAULT NULL COMMENT '资源ID，导入任务未绑定资源时可为空',
  `import_task_id` BIGINT NULL DEFAULT NULL COMMENT '导入任务ID，资源直接上传时可为空',
  `parent_zip_file_id` BIGINT NULL DEFAULT NULL COMMENT '父ZIP文件ID，非ZIP子文件时为空',
  `file_name` VARCHAR(255) NOT NULL COMMENT '当前系统内文件名或展示名',
  `original_file_name` VARCHAR(255) NOT NULL COMMENT '原始上传文件名',
  `file_ext` VARCHAR(20) NOT NULL DEFAULT '' COMMENT '原始扩展名',
  `detected_type` VARCHAR(30) NOT NULL DEFAULT 'unknown' COMMENT '检测后的业务文件类型',
  `mime_type` VARCHAR(120) NOT NULL DEFAULT '' COMMENT 'Tika检测得到的MIME类型',
  `file_size` BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小，单位字节',
  `storage_path` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '原始文件存储路径，仅后台内部使用',
  `archive_entry_path` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '若来自ZIP，记录ZIP内部路径',
  `source_type` VARCHAR(20) NOT NULL DEFAULT 'upload' COMMENT '来源类型：upload、extracted',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '文件排序值，数值越大越靠前',
  `is_primary` TINYINT NOT NULL DEFAULT 0 COMMENT '是否主展示文件：0否 1是',
  `preview_status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '预览状态：pending、processing、success、failed、unsupported',
  `preview_page_count` INT NOT NULL DEFAULT 0 COMMENT '预览页数，最多2页',
  `preview_error_message` VARCHAR(1000) NOT NULL DEFAULT '' COMMENT '预览失败原因',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='资源文件表';

CREATE TABLE IF NOT EXISTS `resource_file_preview` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_file_id` BIGINT NOT NULL COMMENT '资源文件ID',
  `page_no` INT NOT NULL DEFAULT 1 COMMENT '页码，从1开始，最多2页',
  `preview_type` VARCHAR(20) NOT NULL DEFAULT 'image' COMMENT '预览类型：image、text',
  `preview_image_url` VARCHAR(500) NOT NULL DEFAULT '' COMMENT '预览图片地址',
  `preview_text_excerpt` TEXT NULL COMMENT '文本预览摘要',
  `width` INT NULL DEFAULT NULL COMMENT '预览宽度',
  `height` INT NULL DEFAULT NULL COMMENT '预览高度',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序值，通常与页码一致',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件预览表';

CREATE TABLE IF NOT EXISTS `resource_file_process_log` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_file_id` BIGINT NOT NULL COMMENT '资源文件ID',
  `step_name` VARCHAR(50) NOT NULL COMMENT '处理步骤：detect_type、unzip、convert_pdf、render_preview、save_preview',
  `step_status` VARCHAR(20) NOT NULL COMMENT '步骤状态：processing、success、failed、skipped',
  `message` TEXT NOT NULL COMMENT '处理日志信息',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文件处理日志表';
