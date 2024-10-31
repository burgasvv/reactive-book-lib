
create table if not exists authority(
    id bigserial primary key,
    name varchar not null
);

create table if not exists identity(
    id bigserial primary key ,
    username varchar not null ,
    password varchar not null ,
    email varchar not null ,
    enabled boolean not null ,
    authority_id bigserial references authority(id)
        on UPDATE cascade on DELETE cascade
);

insert into authority(name) values ('USER');
insert into authority(name) values ('ADMIN');

insert into identity(username, password, email, authority_id, enabled)
values ('admin', '$2a$10$MoN3NPsWEsIagvRv.a5twus02dZKXzmW806.CyJU9Vowvh28oJO82',
        'burgassme@gmail.com', 2, true);
insert into identity(username, password, email, authority_id, enabled)
values ('user', '$2a$10$SuY8h1Tl3kJbmaEA6z8/EeopEXdudI.7OjD5pLaHaFgPG4P.Fx0jm',
        'user@gmail.com', 1, true);