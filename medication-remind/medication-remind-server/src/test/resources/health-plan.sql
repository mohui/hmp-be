-- 一年3月, 1年5天
-- 健康方案主表 频率开始时间为当前时间减去3天
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000011, 0, '完成本周饮食计划', '白菜', '一年3月,一天两次', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 3 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');
-- 一年3月
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861000, 10000011, NULL, 1, 'YEARS', 3, 'MONTHS', 3, 0, NOW());
-- 一月5天
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861001, 10000011, NULL, 1, 'YEARS', 5, 'DAYS', 5, 0, NOW());
-- 当天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100000, 10000011, NULL, date_add(now(), interval 1 SECOND), 0, date_add(now(), interval 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100001, 10000011, NULL, date_add(now(), interval 1 DAY), 0, date_add(now(), interval 1 DAY));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100002, 10000011, NULL, date_add(now(), interval 2 DAY), 0, date_add(now(), interval 2 DAY));
-- 下一月
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100003, 10000011, NULL, date_add(now(), interval 1 MONTH), 0, date_add(now(), interval 1 MONTH));
-- 加2月
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100004, 10000011, NULL, date_add(now(), interval 2 MONTH), 0, date_add(now(), interval 2 MONTH));
-- 加3月
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100005, 10000011, NULL, date_add(now(), interval 3 MONTH), 0, date_add(now(), interval 3 MONTH));


-- 一年几次, 一月几次
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000012, 0, '完成本周饮食计划', '白菜', '一年3月,一天两次', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 3 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');
-- 一年3次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861010, 10000012, NULL, 1, 'YEARS', 3, 'SEQUENCE', 3, 0, NOW());
-- 一月5次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861011, 10000012, NULL, 1, 'MONTHS', 5, 'SEQUENCE', 5, 0, NOW());

-- 当天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100010, 10000012, NULL, date_add(now(), interval 1 SECOND), 0, date_add(now(), interval 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100011, 10000012, NULL, date_add(now(), interval 2 SECOND), 0, date_add(now(), interval 2 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100012, 10000012, NULL, date_add(now(), interval 3 SECOND), 0, date_add(now(), interval 3 SECOND));
-- 加n天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100013, 10000012, NULL, date_add(now(), interval 1 DAY), 0, date_add(now(), interval 1 DAY));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100014, 10000012, NULL, date_add(now(), interval 2 DAY), 0, date_add(now(), interval 2 DAY));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100015, 10000012, NULL, date_add(now(), interval 3 DAY), 0, date_add(now(), interval 3 DAY));


-- 一月几天, 一周几次
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000013, 0, '完成本周饮食计划', '白菜', '一年3月,一天两次', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 3 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');
-- 一月3天
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861020, 10000013, NULL, 1, 'MONTHS', 3, 'DAYS', 3, 0, NOW());
-- 一周5次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861021, 10000013, NULL, 1, 'WEEKS', 5, 'SEQUENCE', 5, 0, NOW());

-- 减一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100020, 10000013, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100021, 10000013, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND));
-- 当天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100022, 10000013, NULL, date_add(now(), interval 1 SECOND), 0, date_add(now(), interval 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100023, 10000013, NULL, date_add(now(), interval 2 SECOND), 0, date_add(now(), interval 2 SECOND));
-- 加一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100024, 10000013, NULL, date_add(now(), interval 1 day), 0, date_add(now(), interval 1 day));
-- 加2天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484100025, 10000013, NULL, date_add(now(), interval 2 day), 0, date_add(now(), interval 2 day));


-- 4周2周 1周3天, 3天2次
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000014, 0, '完成本周饮食计划', '白菜', '4周2周，1周3天', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 2 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');
-- 4周2周，1周3天
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220014000000001, 10000014, NULL, 4, 'WEEKS', 2, 'WEEKS', 4, 0, NOW());
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220014000000002, 10000014, 1574220014000000001, 1, 'WEEKS', 3, 'DAYS', 3, 0, NOW());
-- 3天2次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220014000000003, 10000014, NULL, 3, 'DAYS', 2, 'SEQUENCE', 2, 0, NOW());

-- 减一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000001, 10000014, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000002, 10000014, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND));
-- 当天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000003, 10000014, NULL, date_add(now(), interval 1 SECOND), 0, date_add(now(), interval 1 SECOND));

-- 加一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000004, 10000014, NULL, date_add(now(), interval 1 day), 0, date_add(now(), interval 2 day));
-- 加6天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000005, 10000014, NULL, date_add(now(), interval 6 day), 0, date_add(now(), interval 2 day));
-- 加7天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000006, 10000014, NULL, date_add(now(), interval 7 day), 0, date_add(now(), interval 2 day));
-- 加8天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220014100000007, 10000014, NULL, date_add(now(), interval 8 day), 0, date_add(now(), interval 2 day));



-- 一周4天, 1天2次
-- 健康方案主表 频率开始时间为当前时间减去3天
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000015, 0, '完成本周饮食计划', '菠菜', '一周7天,一天两次', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 3 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');

-- 健康方案频率表
-- 一周4天 1天2次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861568, 10000015, NULL, 1, 'WEEKS', 4, 'DAYS', 4, 0, NOW());
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861569, 10000015, 1574221477018861568, 1, 'DAYS', 2, 'SEQUENCE', 2, 0, NOW());
-- 一天2次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574221477018861570, 10000015, NULL, 1, 'DAYS', 2, 'SEQUENCE', 2, 0, NOW());


-- 减两天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141030, 10000015, NULL, date_add(date_sub(NOW(),INTERVAL 2 day), INTERVAL 1 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 2 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141031, 10000015, NULL, date_add(date_sub(NOW(),INTERVAL 2 day), INTERVAL 2 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 2 day), INTERVAL 2 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141032, 10000015, NULL, date_add(date_sub(NOW(),INTERVAL 2 day), INTERVAL 3 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 2 day), INTERVAL 3 SECOND));
-- 减一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141040, 10000015, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141041, 10000015, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141042, 10000015, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 3 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 3 SECOND));
-- 当天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141050, 10000015, NULL, date_add(now(), interval 1 SECOND), 0, date_add(now(), interval 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141051, 10000015, NULL, date_add(now(), interval 2 SECOND), 0, date_add(now(), interval 2 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141052, 10000015, NULL, date_add(now(), interval 3 SECOND), 0, date_add(now(), interval 3 SECOND));
-- 加一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141060, 10000015, NULL, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND), 0, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141061, 10000015, NULL, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND), 0, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574222428484141062, 10000015, NULL, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 3 SECOND), 0, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 3 SECOND));


-- 4周4周 1周2天 1天2次, 2周3天，1天2次
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000016, 0, '完成本周饮食计划', '白菜', '4周2周，1周3天', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 2 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');
-- 4周4周，1周2天，1天2次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220016000000001, 10000016, NULL, 4, 'WEEKS', 4, 'WEEKS', 4, 0, NOW());
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220016000000002, 10000016, 1574220016000000001, 1, 'WEEKS', 2, 'DAYS', 3, 0, NOW());
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220016000000003, 10000016, 1574220016000000002, 1, 'DAYS', 2, 'SEQUENCE', 2, 0, NOW());
-- 2周3天，1天2次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220016000000004, 10000016, NULL, 2, 'WEEKS', 3, 'DAYS', 3, 0, NOW());
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220016000000005, 10000016, 1574220016000000004, 1, 'DAYS', 2, 'SEQUENCE', 2, 0, NOW());

-- 减一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000001, 10000016, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000002, 10000016, NULL, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND), 0, date_add(date_sub(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND));
-- 当天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000003, 10000016, NULL, date_add(now(), interval 1 SECOND), 0, date_add(now(), interval 1 SECOND));

-- 加一天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000004, 10000016, NULL, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND), 0, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000104, 10000016, NULL, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND), 0, date_add(date_add(NOW(),INTERVAL 1 day), INTERVAL 2 SECOND));
-- 加6天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000005, 10000016, NULL, date_add(now(), interval 6 day), 0, date_add(now(), interval 2 day));
-- 加7天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000006, 10000016, NULL, date_add(date_add(NOW(),INTERVAL 7 day), INTERVAL 1 SECOND), 0, date_add(date_add(NOW(),INTERVAL 7 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000007, 10000016, NULL, date_add(date_add(NOW(),INTERVAL 7 day), INTERVAL 2 SECOND), 0, date_add(date_add(NOW(),INTERVAL 7 day), INTERVAL 2 SECOND));
-- 加8天
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000008, 10000016, NULL, date_add(date_add(NOW(),INTERVAL 8 day), INTERVAL 1 SECOND), 0, date_add(date_add(NOW(),INTERVAL 8 day), INTERVAL 1 SECOND));
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220016100000009, 10000016, NULL, date_add(date_add(NOW(),INTERVAL 8 day), INTERVAL 2 SECOND), 0, date_add(date_add(NOW(),INTERVAL 8 day), INTERVAL 2 SECOND));

-- 1天3次
INSERT INTO `mr_health_plan`
(`kn_id`, `kn_patient_id`, `kn_name`, `kn_sub_name`, `kn_desc`, `kn_type`,
 `kn_time`, `is_monday`, `is_tuesday`, `is_wednesday`, `is_thursday`, `is_friday`, `is_saturday`, `is_sunday`,
 `kn_cycle_start_time`, `kn_cycle_end_time`,
 `is_used`, `is_del`, `kn_display_time`,
 `kn_created_by`, `kn_created_at`, `kn_updated_by`, `kn_updated_at`, `external_key`)
VALUES (10000017, 0, '完成本周饮食计划', '白菜', '1天4次', 'DIET_PLAN',
        NULL, 0, 0, 0, 0, 0, 0, 0,
        DATE_SUB(DATE_FORMAT(CURDATE(),'%Y-%m-%d %H:%i:%s'), INTERVAL 2 DAY), NULL,
        1, 0, CURDATE(),
        0, NOW(), 0,  NOW(), 'HYPERTENSION');
-- 1天3次
INSERT INTO `mr_frequency` (`kn_id`, `kn_health_plan_id`, `kn_explain_id`, `kn_frequency_time`, `kn_frequency_time_unit`, `kn_frequency_num`, `kn_frequency_num_unit`, `kn_frequency_max_num`, `kn_created_by`, `kn_created_at`)
VALUES (1574220017000000001, 10000017, null, 1, 'DAYS', 3, 'SEQUENCE', 3, 0, NOW());

INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220017100000001, 10000017, NULL, DATE_ADD(CURDATE(), INTERVAL 9 HOUR), 0, now());
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220017100000002, 10000017, NULL, DATE_ADD(CURDATE(), INTERVAL 9 HOUR), 0, now());
INSERT INTO `mr_clock_in` (`kn_id`, `kn_health_plan_id`, `kn_time`, `kn_clock_at`, `kn_created_by`, `kn_created_at`) VALUES (1574220017100000003, 10000017, NULL, DATE_ADD(CURDATE(), INTERVAL 9 HOUR), 0, now());
