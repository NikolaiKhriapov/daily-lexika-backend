-- Remove 'nameChineseTraditional'
alter table word_data
    drop column name_chinese_traditional;

-- Rename 'pinyin' to 'transcription'
alter table word_data
    rename column pinyin to transcription;

-- Add 'definition' and 'examples' fields
alter table word_data
    add column definition text,
    add column examples text;
