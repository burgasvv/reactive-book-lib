
create table if not exists subscription(
    id serial primary key ,
    title varchar(250) not null ,
    active boolean not null ,
    created timestamp not null ,
    updated timestamp ,
    ended timestamp ,
    paid boolean not null ,
    identity_id serial unique references identity(id)
        on DELETE cascade on UPDATE cascade
);

create table if not exists subscription_book (
    subscription_id serial references subscription(id)
        on DELETE cascade on UPDATE cascade ,
    book_id serial references book(id)
        on DELETE cascade on UPDATE cascade,
    primary key (subscription_id, book_id)
);

insert into subscription(title, active, created, paid, identity_id)
VALUES ('admin-sub', true, '2004-10-19 10:23:54', true, 1);