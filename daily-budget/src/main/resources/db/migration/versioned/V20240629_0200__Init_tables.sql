create table users
(
    id                   bigint                   not null primary key,
    email                varchar(100)             not null unique,
    password             varchar(255)             not null,
    interface_language   varchar(30),
    date_of_registration timestamp with time zone not null
);
create sequence user_id_sequence;

create table logs
(
    id         bigint                   not null primary key,
    user_id    bigint                   not null,
    user_email varchar(100)             not null,
    action     varchar(50)              not null,
    timestamp  timestamp with time zone not null,
    comment    varchar(255)
);
create sequence log_id_sequence;

create table accounts
(
    id            bigint         not null primary key,
    user_id       bigint         not null,
    name          varchar(20)    not null,
    amount        numeric(12, 2) not null,
    currency_code varchar(3)     not null,
    color         varchar(20)    not null,
    is_active     boolean        not null
);
create sequence account_id_sequence;

create table expense_operations
(
    id            bigint                   not null primary key,
    user_id       bigint                   not null,
    amount        numeric(12, 2)           not null,
    currency_code varchar(3)               not null,
    account_id    bigint                   not null,
    category      varchar(20)              not null,
    timestamp     timestamp with time zone not null,
    comment       varchar(255)
);
create sequence expense_operation_id_sequence;
