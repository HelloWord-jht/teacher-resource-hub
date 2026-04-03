SET NAMES utf8mb4;
USE `teacher_resource_hub`;

UPDATE `resource`
SET
  `preview_available_status` = CASE
    WHEN `preview_count` > 0 THEN 'available'
    ELSE 'none'
  END,
  `preview_mode` = 'single'
WHERE `is_deleted` = 0;

INSERT INTO `resource_file` (
  `resource_id`,
  `import_task_id`,
  `parent_zip_file_id`,
  `file_name`,
  `original_file_name`,
  `file_ext`,
  `detected_type`,
  `mime_type`,
  `file_size`,
  `storage_path`,
  `archive_entry_path`,
  `source_type`,
  `sort_order`,
  `is_primary`,
  `preview_status`,
  `preview_page_count`,
  `preview_error_message`,
  `created_at`,
  `updated_at`,
  `is_deleted`
)
SELECT
  `r`.`id`,
  NULL,
  NULL,
  '历史样张',
  '历史样张',
  'png',
  'image',
  'image/png',
  0,
  '',
  '',
  'upload',
  0,
  1,
  'success',
  LEAST(COUNT(`rp`.`id`), 2),
  '',
  NOW(),
  NOW(),
  0
FROM `resource` `r`
INNER JOIN `resource_preview` `rp`
  ON `rp`.`resource_id` = `r`.`id`
LEFT JOIN `resource_file` `rf_exist`
  ON `rf_exist`.`resource_id` = `r`.`id`
 AND `rf_exist`.`is_deleted` = 0
WHERE `r`.`is_deleted` = 0
  AND `rf_exist`.`id` IS NULL
GROUP BY `r`.`id`;

UPDATE `resource` `r`
INNER JOIN `resource_file` `rf`
  ON `rf`.`resource_id` = `r`.`id`
 AND `rf`.`is_primary` = 1
 AND `rf`.`is_deleted` = 0
SET `r`.`primary_file_id` = `rf`.`id`
WHERE `r`.`primary_file_id` IS NULL;

INSERT INTO `resource_file_preview` (
  `resource_file_id`,
  `page_no`,
  `preview_type`,
  `preview_image_url`,
  `preview_text_excerpt`,
  `width`,
  `height`,
  `sort_order`,
  `created_at`,
  `updated_at`
)
WITH `ranked_preview` AS (
  SELECT
    `rp`.`resource_id`,
    `rp`.`image_url`,
    ROW_NUMBER() OVER (
      PARTITION BY `rp`.`resource_id`
      ORDER BY `rp`.`sort_order` DESC, `rp`.`id` ASC
    ) AS `rn`
  FROM `resource_preview` `rp`
)
SELECT
  `rf`.`id`,
  `ranked_preview`.`rn`,
  'image',
  `ranked_preview`.`image_url`,
  NULL,
  NULL,
  NULL,
  `ranked_preview`.`rn`,
  NOW(),
  NOW()
FROM `ranked_preview`
INNER JOIN `resource_file` `rf`
  ON `rf`.`resource_id` = `ranked_preview`.`resource_id`
 AND `rf`.`original_file_name` = '历史样张'
 AND `rf`.`is_primary` = 1
 AND `rf`.`is_deleted` = 0
LEFT JOIN `resource_file_preview` `rfp_exist`
  ON `rfp_exist`.`resource_file_id` = `rf`.`id`
 AND `rfp_exist`.`page_no` = `ranked_preview`.`rn`
WHERE `ranked_preview`.`rn` <= 2
  AND `rfp_exist`.`id` IS NULL;

UPDATE `resource` `r`
SET `preview_available_status` = CASE
  WHEN EXISTS (
    SELECT 1
    FROM `resource_file` `rf`
    WHERE `rf`.`resource_id` = `r`.`id`
      AND `rf`.`preview_status` = 'success'
      AND `rf`.`is_deleted` = 0
  ) THEN 'available'
  ELSE 'none'
END
WHERE `r`.`is_deleted` = 0;
