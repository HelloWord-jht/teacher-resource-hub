SET NAMES utf8mb4;
USE `teacher_resource_hub`;

CREATE UNIQUE INDEX `uk_admin_user_username` ON `admin_user` (`username`);
CREATE INDEX `idx_admin_user_status_deleted` ON `admin_user` (`status`, `is_deleted`);

CREATE UNIQUE INDEX `uk_category_name` ON `category` (`name`);
CREATE UNIQUE INDEX `uk_category_slug` ON `category` (`slug`);
CREATE INDEX `idx_category_status_sort` ON `category` (`status`, `is_deleted`, `sort_order`);

CREATE UNIQUE INDEX `uk_tag_name` ON `tag` (`name`);
CREATE INDEX `idx_tag_status_sort` ON `tag` (`status`, `is_deleted`, `sort_order`);

CREATE UNIQUE INDEX `uk_resource_slug` ON `resource` (`slug`);
CREATE INDEX `idx_resource_category_status` ON `resource` (`category_id`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_grade_status` ON `resource` (`grade`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_scene_status` ON `resource` (`scene`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_publish_time` ON `resource` (`publish_time`);
CREATE INDEX `idx_resource_recommended_sort` ON `resource` (`is_recommended`, `status`, `is_deleted`, `sort_order`, `publish_time`);
CREATE INDEX `idx_resource_hot_sort` ON `resource` (`view_count`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_title` ON `resource` (`title`);
CREATE INDEX `idx_resource_sort_order` ON `resource` (`sort_order`);

CREATE INDEX `idx_resource_preview_resource_sort` ON `resource_preview` (`resource_id`, `sort_order`);

CREATE UNIQUE INDEX `uk_resource_tag_pair` ON `resource_tag` (`resource_id`, `tag_id`);
CREATE INDEX `idx_resource_tag_tag` ON `resource_tag` (`tag_id`);

CREATE UNIQUE INDEX `uk_resource_recommend_pair` ON `resource_recommend` (`resource_id`, `recommended_resource_id`);
CREATE INDEX `idx_resource_recommend_resource_sort` ON `resource_recommend` (`resource_id`, `sort_order`);
CREATE INDEX `idx_resource_recommend_target` ON `resource_recommend` (`recommended_resource_id`);

CREATE INDEX `idx_faq_status_sort` ON `faq` (`status`, `is_deleted`, `sort_order`);

CREATE INDEX `idx_lead_status_created` ON `lead` (`status`, `is_deleted`, `created_at`);
CREATE INDEX `idx_lead_source_page` ON `lead` (`source_page`);
CREATE INDEX `idx_lead_contact` ON `lead` (`contact`);

CREATE UNIQUE INDEX `uk_site_config_group_key` ON `site_config` (`config_group`, `config_key`);
CREATE INDEX `idx_site_config_group` ON `site_config` (`config_group`);

CREATE UNIQUE INDEX `uk_page_content_code` ON `page_content` (`page_code`);
