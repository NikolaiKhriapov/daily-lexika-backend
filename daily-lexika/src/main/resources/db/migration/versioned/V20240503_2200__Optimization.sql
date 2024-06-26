ALTER TABLE words
    ALTER COLUMN id TYPE BIGINT;

ALTER TABLE words
    ALTER COLUMN user_id TYPE INTEGER;

ALTER TABLE words
    ALTER COLUMN word_data_id TYPE INTEGER;

ALTER TABLE words
    ALTER COLUMN status TYPE VARCHAR(20);

ALTER TABLE words
    ALTER COLUMN current_streak TYPE SMALLINT;

ALTER TABLE words
    ALTER COLUMN total_streak TYPE SMALLINT;

ALTER TABLE words
    ALTER COLUMN occurrence TYPE SMALLINT;


ALTER TABLE word_data
    ALTER COLUMN id TYPE INTEGER;

ALTER TABLE word_data
    RENAME COLUMN name_chinese_simplified TO name_chinese;


ALTER TABLE users
    ALTER COLUMN id TYPE INTEGER;


ALTER TABLE users_role_statistics
    ALTER COLUMN users_id TYPE INTEGER;


ALTER TABLE word_data_list_of_word_packs
    ALTER COLUMN word_data_id TYPE INTEGER;


ALTER TABLE notifications
    ALTER COLUMN notification_id TYPE INTEGER;

ALTER TABLE notifications
    ALTER COLUMN to_user_id TYPE INTEGER;


ALTER TABLE reviews
    ALTER COLUMN user_id TYPE INTEGER;
