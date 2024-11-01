
create table if not exists author (
    id serial primary key ,
    firstname varchar not null ,
    lastname varchar not null ,
    patronymic varchar not null ,
    birth_date date ,
    death_date date ,
    biography text
);

create table if not exists genre (
    id serial primary key ,
    name varchar not null ,
    description text
);

create table if not exists book (
    id serial primary key ,
    title varchar not null ,
    description text ,
    author_id serial references author(id)
        on UPDATE cascade on DELETE cascade ,
    genre_id serial references genre(id)
        on UPDATE cascade on DELETE cascade
);