create table word_data
(
    id                       bigint not null primary key,
    name_chinese_simplified  varchar(20),
    name_chinese_traditional varchar(20),
    name_english             varchar(255),
    name_russian             varchar(255),
    pinyin                   varchar(50),
    platform                 varchar(50)
);

create table word_packs
(
    category    varchar(50),
    description varchar(255),
    name        varchar(50) not null primary key,
    platform    varchar(50)
);

create table users
(
    id       bigint       not null primary key,
    email    varchar(100) not null unique,
    name     varchar(50) not null,
    password varchar(255) not null,
    role     varchar(50)
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
    word_data_id            bigint
        references word_data,
    status                  varchar(20)
);
create sequence word_id_sequence;

create table notifications
(
    is_read         boolean,
    notification_id bigint not null primary key,
    sent_at         timestamp(6),
    to_user_id      bigint,
    message         text,
    sender          varchar(100),
    subject         varchar(100),
    to_user_email   varchar(100)
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
    word_pack_name           varchar(50)
        references word_packs
);
create sequence review_id_sequence;

create table reviews_list_of_words
(
    list_of_words_order integer not null,
    list_of_words_id    bigint  not null
        references words,
    reviews_id          bigint  not null
        references reviews,
    primary key (list_of_words_order, reviews_id)
);

create table word_data_list_of_word_packs
(
    word_data_id            bigint       not null
        references word_data,
    list_of_word_packs_name varchar(100) not null
        references word_packs
);

create table role_statistics
(
    id                  bigint not null primary key,
    current_streak      bigint,
    date_of_last_streak date,
    record_streak       bigint,
    role_name           varchar(50)
);
create sequence role_statistics_id_sequence;

create table users_role_statistics
(
    role_statistics_id bigint not null
        references role_statistics,
    users_id           bigint not null
        references users,
    primary key (role_statistics_id, users_id)
);
