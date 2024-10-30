
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
values ('burgasvv', '$2a$10$WAXGuzDVROFEFv9jUr3qZuXBhKXCuzAWLF.Ia6jmBHgvSEuYxnzZ6',
        'burgassme@gmail.com', 2, true);
insert into identity(username, password, email, authority_id, enabled)
values ('baldahin', '$2a$10$HI77vdVimvu6z2WrQXpazO9zI/NprffkBvqjYvY5g9zZyyPXmO3I2',
        'baldahin@gmail.com', 1, true);