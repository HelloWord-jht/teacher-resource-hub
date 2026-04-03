SET NAMES utf8mb4;
USE `teacher_resource_hub`;

CREATE UNIQUE INDEX `uk_admin_user_username` ON `admin_user` (`username`);
CREATE INDEX `idx_admin_user_status_deleted` ON `admin_user` (`status`, `is_deleted`);

CREATE UNIQUE INDEX `uk_category_name` ON `category` (`name`);
CREATE UNIQUE INDEX `uk_category_code` ON `category` (`code`);
CREATE UNIQUE INDEX `uk_category_slug` ON `category` (`slug`);
CREATE INDEX `idx_category_status_sort` ON `category` (`status`, `is_deleted`, `sort_order`);

CREATE UNIQUE INDEX `uk_tag_name` ON `tag` (`name`);
CREATE INDEX `idx_tag_status_sort` ON `tag` (`status`, `is_deleted`, `sort_order`);

CREATE INDEX `idx_resource_source_auth` ON `resource_source` (`authorization_status`, `risk_level`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_source_type_status` ON `resource_source` (`source_type`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_source_owner` ON `resource_source` (`owner_name`);

CREATE UNIQUE INDEX `uk_resource_code` ON `resource` (`resource_code`);
CREATE UNIQUE INDEX `uk_resource_slug` ON `resource` (`slug`);
CREATE INDEX `idx_resource_category_status` ON `resource` (`category_id`, `status`, `is_deleted`, `authorization_status_snapshot`);
CREATE INDEX `idx_resource_source_status` ON `resource` (`source_id`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_grade_status` ON `resource` (`grade`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_scene_status` ON `resource` (`scene`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_publish_time` ON `resource` (`publish_time`);
CREATE INDEX `idx_resource_recommended_sort` ON `resource` (`is_recommended`, `status`, `is_deleted`, `authorization_status_snapshot`, `sort_order`, `publish_time`);
CREATE INDEX `idx_resource_hot_sort` ON `resource` (`view_count`, `status`, `is_deleted`, `authorization_status_snapshot`);
CREATE INDEX `idx_resource_consult_sort` ON `resource` (`consult_count`, `status`, `is_deleted`, `authorization_status_snapshot`);
CREATE INDEX `idx_resource_sort_order` ON `resource` (`sort_order`);
CREATE INDEX `idx_resource_title` ON `resource` (`title`);
CREATE INDEX `idx_resource_auth_snapshot` ON `resource` (`authorization_status_snapshot`, `status`, `is_deleted`);
CREATE INDEX `idx_resource_search_keywords` ON `resource` (`search_keywords`(255));

CREATE INDEX `idx_resource_preview_resource_sort` ON `resource_preview` (`resource_id`, `sort_order`);

CREATE UNIQUE INDEX `uk_resource_tag_pair` ON `resource_tag` (`resource_id`, `tag_id`);
CREATE INDEX `idx_resource_tag_tag` ON `resource_tag` (`tag_id`);

CREATE UNIQUE INDEX `uk_resource_recommend_pair` ON `resource_recommend` (`resource_id`, `recommended_resource_id`);
CREATE INDEX `idx_resource_recommend_resource_sort` ON `resource_recommend` (`resource_id`, `sort_order`);
CREATE INDEX `idx_resource_recommend_target` ON `resource_recommend` (`recommended_resource_id`);

CREATE INDEX `idx_faq_status_sort` ON `faq` (`status`, `is_deleted`, `sort_order`);

CREATE UNIQUE INDEX `uk_lead_channel_key` ON `lead_channel` (`channel_key`);
CREATE UNIQUE INDEX `uk_lead_channel_name` ON `lead_channel` (`channel_name`);
CREATE INDEX `idx_lead_channel_status_sort` ON `lead_channel` (`status`, `is_deleted`, `sort_order`);

CREATE UNIQUE INDEX `uk_content_campaign_tracking_code` ON `content_campaign` (`tracking_code`);
CREATE INDEX `idx_content_campaign_channel_publish` ON `content_campaign` (`channel_id`, `status`, `is_deleted`, `publish_time`);
CREATE INDEX `idx_content_campaign_target_resource_code` ON `content_campaign` (`target_resource_code`);
CREATE INDEX `idx_content_campaign_target_resource_id` ON `content_campaign` (`target_resource_id`);

CREATE INDEX `idx_lead_status_created` ON `lead` (`status`, `is_deleted`, `created_at`);
CREATE INDEX `idx_lead_channel_created` ON `lead` (`channel`, `is_deleted`, `created_at`);
CREATE INDEX `idx_lead_tracking_code` ON `lead` (`tracking_code`);
CREATE INDEX `idx_lead_target_resource_code` ON `lead` (`target_resource_code`);
CREATE INDEX `idx_lead_target_resource_id` ON `lead` (`target_resource_id`);
CREATE INDEX `idx_lead_wechat_deal` ON `lead` (`wechat_added_status`, `deal_status`, `created_at`);
CREATE INDEX `idx_lead_source_page` ON `lead` (`source_page`);
CREATE INDEX `idx_lead_contact` ON `lead` (`contact`);

CREATE UNIQUE INDEX `uk_resource_storage_resource_id` ON `resource_storage` (`resource_id`);
CREATE INDEX `idx_resource_storage_platform_status` ON `resource_storage` (`storage_platform`, `status`, `is_deleted`);

CREATE INDEX `idx_import_task_status_executed` ON `import_task` (`import_status`, `executed_at`);
CREATE INDEX `idx_import_task_generated_resource_code` ON `import_task` (`generated_resource_code`);
CREATE INDEX `idx_import_task_operator_created` ON `import_task` (`operator_name`, `created_at`);

CREATE INDEX `idx_delivery_record_resource_code_created` ON `delivery_record` (`resource_code`, `created_at`);
CREATE INDEX `idx_delivery_record_resource_id_created` ON `delivery_record` (`resource_id`, `created_at`);
CREATE INDEX `idx_delivery_record_lead_id_created` ON `delivery_record` (`lead_id`, `created_at`);
CREATE INDEX `idx_delivery_record_operator_created` ON `delivery_record` (`operator_name`, `created_at`);

CREATE INDEX `idx_resource_search_log_keyword_created` ON `resource_search_log` (`keyword`, `created_at`);
CREATE INDEX `idx_resource_search_log_type_created` ON `resource_search_log` (`search_type`, `created_at`);
CREATE INDEX `idx_resource_search_log_matched_resource` ON `resource_search_log` (`matched_resource_id`, `created_at`);

CREATE INDEX `idx_visit_trace_tracking_created` ON `visit_trace` (`tracking_code`, `created_at`);
CREATE INDEX `idx_visit_trace_channel_created` ON `visit_trace` (`channel`, `created_at`);
CREATE INDEX `idx_visit_trace_target_resource_code` ON `visit_trace` (`target_resource_code`, `created_at`);
CREATE INDEX `idx_visit_trace_target_resource_id` ON `visit_trace` (`target_resource_id`, `created_at`);
CREATE INDEX `idx_visit_trace_client_created` ON `visit_trace` (`client_id`, `created_at`);

CREATE UNIQUE INDEX `uk_site_config_group_key` ON `site_config` (`config_group`, `config_key`);
CREATE INDEX `idx_site_config_group` ON `site_config` (`config_group`);

CREATE UNIQUE INDEX `uk_page_content_code` ON `page_content` (`page_code`);
