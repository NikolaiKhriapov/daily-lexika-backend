create table users
(
    id       integer      not null primary key,
    name     varchar(50)  not null,
    email    varchar(100) not null unique,
    password varchar(255) not null,
    role     varchar(50)
);
create sequence user_id_sequence;

insert into users (id, name, email, password, role)
values (1, 'Nikolai', 'kolyakhryapov@gmail.com', '$2a$10$ojMXJor3nj5k2ivzi9.Dee2.HBkKNGQ.HfcM5xAMgu79ebwdaPvce', 'SUPER_ADMIN');
