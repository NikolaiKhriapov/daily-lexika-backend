create table logs
(
    id         bigint not null primary key,
    user_id    integer not null,
    user_email varchar(100) not null,
    action     varchar(50) not null,
    platform   varchar(50) not null,
    timestamp  timestamp with time zone not null
);
create sequence log_id_sequence;

alter table users
    add column date_of_registration timestamp with time zone not null default '2024-05-01 00:00:00.000000 +00:00';

