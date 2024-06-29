create table users
(
    id                   integer                  not null primary key,
    email                varchar(100)             not null unique,
    password             varchar(255)             not null,
    interface_language   varchar(30),
    date_of_registration timestamp with time zone not null
);
create sequence user_id_sequence;

create table logs
(
    id         bigint                   not null primary key,
    user_id    integer                  not null,
    user_email varchar(100)             not null,
    action     varchar(50)              not null,
    timestamp  timestamp with time zone not null,
    comment    varchar(255)
);
create sequence log_id_sequence;
