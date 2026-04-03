SET NAMES utf8mb4;
USE `teacher_resource_hub`;

ALTER TABLE `resource`
  ADD INDEX `idx_resource_primary_file` (`primary_file_id`),
  ADD INDEX `idx_resource_preview_status_mode` (`preview_available_status`, `preview_mode`, `status`, `is_deleted`);

ALTER TABLE `import_task`
  ADD INDEX `idx_import_task_type_status` (`import_type`, `import_status`, `updated_at`),
  ADD INDEX `idx_import_task_file_counts` (`preview_success_count`, `preview_failed_count`, `updated_at`);

CREATE INDEX `idx_resource_file_resource_sort` ON `resource_file` (`resource_id`, `is_deleted`, `sort_order`, `id`);
CREATE INDEX `idx_resource_file_import_task_sort` ON `resource_file` (`import_task_id`, `is_deleted`, `sort_order`, `id`);
CREATE INDEX `idx_resource_file_parent_zip_sort` ON `resource_file` (`parent_zip_file_id`, `is_deleted`, `sort_order`, `id`);
CREATE INDEX `idx_resource_file_preview_status` ON `resource_file` (`preview_status`, `is_deleted`, `updated_at`);
CREATE INDEX `idx_resource_file_detected_type` ON `resource_file` (`detected_type`, `preview_status`, `is_deleted`);
CREATE INDEX `idx_resource_file_primary` ON `resource_file` (`resource_id`, `is_primary`, `is_deleted`);
CREATE INDEX `idx_resource_file_name` ON `resource_file` (`file_name`);
CREATE INDEX `idx_resource_file_archive_entry` ON `resource_file` (`archive_entry_path`(255));

CREATE UNIQUE INDEX `uk_resource_file_preview_page` ON `resource_file_preview` (`resource_file_id`, `page_no`);
CREATE INDEX `idx_resource_file_preview_sort` ON `resource_file_preview` (`resource_file_id`, `sort_order`);
CREATE INDEX `idx_resource_file_preview_type` ON `resource_file_preview` (`preview_type`);

CREATE INDEX `idx_resource_file_process_log_file_time` ON `resource_file_process_log` (`resource_file_id`, `created_at`);
CREATE INDEX `idx_resource_file_process_log_step_status` ON `resource_file_process_log` (`step_name`, `step_status`, `created_at`);
