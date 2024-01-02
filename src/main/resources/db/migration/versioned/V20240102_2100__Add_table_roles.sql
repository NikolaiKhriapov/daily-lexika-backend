alter table users
    rename column user_role to role;
update users set role = 'USER_CHINESE' where role = 'USER';

alter table word_data
    add column platform varchar(50);

alter table word_packs
    add column platform varchar(50);

create table roles
(
    id        bigint not null primary key,
    role_name varchar(50)
);
create sequence role_id_sequence;

create table users_roles
(
    roles_id bigint not null
        references roles,
    users_id bigint not null
        references users,
    primary key (roles_id, users_id)
);

insert into roles(id, role_name) values (1, 'ADMIN');
insert into roles(id, role_name) values (2, 'USER_ENGLISH');
insert into roles(id, role_name) values (3, 'USER_CHINESE');

insert into users_roles (users_id, roles_id)
select id, 3
from users;
