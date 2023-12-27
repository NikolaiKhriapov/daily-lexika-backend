create table word_data
(
    id                       bigint not null primary key,
    name_chinese_simplified  varchar(255),
    name_chinese_traditional varchar(255),
    name_english             varchar(255),
    name_russian             varchar(255),
    pinyin                   varchar(255)
);

create table word_packs
(
    category    varchar(255),
    description varchar(255),
    name        varchar(255) not null primary key
);

create table users
(
    date_of_last_streak date,
    current_streak      bigint,
    id                  bigint       not null primary key,
    record_streak       bigint,
    email               varchar(255) not null unique,
    name                varchar(255) not null,
    password            varchar(255) not null,
    user_role           varchar(255)
);
create sequence user_id_sequence;

create table words
(
    current_streak          integer,
    date_of_last_occurrence date,
    occurrence              integer,
    total_streak            integer,
    id                      bigint not null primary key,
    user_id                 bigint,
    word_data_id            bigint,
    status                  varchar(255)
);
create sequence word_id_sequence;

create table chinese_characters
(
    correct_streak           integer,
    id                       bigint not null primary key,
    name_chinese_simplified  varchar(255),
    name_chinese_traditional varchar(255),
    name_english             varchar(255),
    name_russian             varchar(255),
    pinyin                   varchar(255),
    status                   varchar(255)
);
create sequence chinese_character_id_sequence;

create table notifications
(
    is_read         boolean,
    notification_id bigint not null primary key,
    sent_at         timestamp(6),
    to_user_id      bigint,
    message         varchar(255),
    sender          varchar(255),
    subject         varchar(255),
    to_user_email   varchar(255)
);
create sequence notification_id_sequence;

create table reviews
(
    date_generated           date,
    date_last_completed      date,
    max_new_words_per_day    integer,
    max_review_words_per_day integer,
    id                       bigint not null primary key,
    user_id                  bigint,
    word_pack_name           varchar(255) unique
        references word_packs
);
create sequence review_id_sequence;

create table reviews_list_of_words
(
    list_of_words_order integer not null,
    list_of_words_id    bigint  not null references words,
    reviews_id          bigint  not null
        references reviews,
    primary key (list_of_words_order, reviews_id)
);

create table word_data_list_of_chinese_characters
(
    list_of_chinese_characters_id bigint not null
        references chinese_characters,
    word_data_id                  bigint not null
        references word_data
);

create table word_data_list_of_word_packs
(
    word_data_id            bigint       not null
        references word_data,
    list_of_word_packs_name varchar(255) not null
        references word_packs
);
