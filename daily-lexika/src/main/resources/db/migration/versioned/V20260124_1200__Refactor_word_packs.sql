ALTER TABLE word_packs ADD COLUMN id bigint;
CREATE SEQUENCE word_pack_id_sequence;
UPDATE word_packs SET id = nextval('word_pack_id_sequence') WHERE id IS NULL;
ALTER TABLE word_packs ALTER COLUMN id SET NOT NULL;
ALTER TABLE word_packs ALTER COLUMN id SET DEFAULT nextval('word_pack_id_sequence');

ALTER TABLE word_packs ADD COLUMN user_id integer;

ALTER TABLE reviews ADD COLUMN word_pack_id bigint;
ALTER TABLE word_data_list_of_word_packs ADD COLUMN word_pack_id bigint;

UPDATE reviews r
SET word_pack_id = wp.id
FROM word_packs wp
WHERE r.word_pack_name = wp.name;

UPDATE word_data_list_of_word_packs wdl
SET word_pack_id = wp.id
FROM word_packs wp
WHERE wdl.list_of_word_packs_name = wp.name;

UPDATE word_packs
SET user_id = CAST(substring(name from '__([0-9]+)$') AS integer)
WHERE category = 'CUSTOM'
  AND name ~ '__[0-9]+$';

ALTER TABLE reviews DROP CONSTRAINT IF EXISTS reviews_word_pack_name_fkey;
ALTER TABLE word_data_list_of_word_packs DROP CONSTRAINT IF EXISTS word_data_list_of_word_packs_list_of_word_packs_name_fkey;

ALTER TABLE reviews DROP COLUMN word_pack_name;
ALTER TABLE word_data_list_of_word_packs DROP COLUMN list_of_word_packs_name;

ALTER TABLE word_packs DROP CONSTRAINT IF EXISTS word_packs_pkey;
ALTER TABLE word_packs ADD CONSTRAINT word_packs_pkey PRIMARY KEY (id);

ALTER TABLE reviews
ADD CONSTRAINT reviews_word_pack_id_fkey
FOREIGN KEY (word_pack_id) REFERENCES word_packs(id);

ALTER TABLE word_data_list_of_word_packs
ADD CONSTRAINT word_data_list_of_word_packs_word_pack_id_fkey
FOREIGN KEY (word_pack_id) REFERENCES word_packs(id);

ALTER TABLE word_data_list_of_word_packs ALTER COLUMN word_pack_id SET NOT NULL;

UPDATE word_packs
SET name = regexp_replace(name, '^(EN__|CH__)', '')
WHERE category <> 'CUSTOM';

UPDATE word_packs
SET name = regexp_replace(regexp_replace(name, '^(EN__|CH__)', ''), '__[0-9]+$', '')
WHERE category = 'CUSTOM';

ALTER TABLE word_packs ADD CONSTRAINT word_packs_platform_name_user_id_key UNIQUE (platform, name, user_id);
ALTER TABLE word_packs ADD CONSTRAINT word_packs_custom_user_id_chk CHECK (category <> 'CUSTOM' OR user_id IS NOT NULL);
ALTER TABLE word_packs
ADD CONSTRAINT word_packs_user_id_fkey
FOREIGN KEY (user_id) REFERENCES users(id);

SELECT setval('word_pack_id_sequence', (SELECT max(id) FROM word_packs));
