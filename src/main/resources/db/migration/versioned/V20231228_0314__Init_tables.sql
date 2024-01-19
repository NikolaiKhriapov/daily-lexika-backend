create table word_data
(
    id                       bigint not null primary key,
    name_chinese_simplified  varchar(20),
    name_chinese_traditional varchar(20),
    pinyin                   varchar(50),
    name_english             varchar(255),
    name_russian             varchar(255),
    platform                 varchar(50)
);

create table word_packs
(
    name        varchar(50) not null primary key,
    description varchar(255),
    category    varchar(50),
    platform    varchar(50)
);

create table users
(
    id       bigint       not null primary key,
    name     varchar(50)  not null,
    email    varchar(100) not null unique,
    password varchar(255) not null,
    role     varchar(50)
);
create sequence user_id_sequence;

create table words
(
    id                      bigint not null primary key,
    user_id                 bigint,
    word_data_id            bigint
        references word_data,
    status                  varchar(20),
    current_streak          integer,
    total_streak            integer,
    occurrence              integer,
    date_of_last_occurrence date
);
create sequence word_id_sequence;

create table notifications
(
    notification_id bigint not null primary key,
    to_user_id      bigint,
    to_user_email   varchar(100),
    sender          varchar(100),
    subject         varchar(100),
    message         text,
    sent_at         timestamp(6),
    is_read         boolean
);
create sequence notification_id_sequence;

create table reviews
(
    id                       bigint not null primary key,
    user_id                  bigint,
    max_new_words_per_day    integer,
    max_review_words_per_day integer,
    word_pack_name           varchar(50)
        references word_packs,
    date_last_completed      date,
    date_generated           date
);
create sequence review_id_sequence;

create table reviews_list_of_words
(
    list_of_words_order integer not null,
    reviews_id          bigint  not null
        references reviews,
    list_of_words_id    bigint  not null
        references words,
    primary key (reviews_id, list_of_words_order)
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
    role_name           varchar(50),
    current_streak      bigint,
    date_of_last_streak date,
    record_streak       bigint
);
create sequence role_statistics_id_sequence;

create table users_role_statistics
(
    users_id           bigint not null
        references users,
    role_statistics_id bigint not null unique
        references role_statistics,
    primary key (users_id, role_statistics_id)
);
