
drop table if exists payment, payment_type, subscription_book, subscription,
    book, genre, author, identity, authority;




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




create table if not exists author (
      id serial primary key ,
      firstname varchar not null ,
      lastname varchar not null ,
      patronymic varchar not null
);

create table if not exists genre (
     id serial primary key ,
     name varchar not null
);

create table if not exists book (
    id serial primary key ,
    title varchar not null ,
    pages serial,
    description text ,
    author_id serial references author(id)
        on UPDATE cascade on DELETE cascade ,
    genre_id serial references genre(id)
        on UPDATE cascade on DELETE cascade
);




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




create table if not exists payment_type(
    id serial primary key ,
    name varchar not null
);

create table if not exists payment (
    id serial primary key ,
    payment_type_id serial references payment_type(id)
        on UPDATE cascade on DELETE cascade ,
    subscription_id serial references subscription(id)
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




insert into genre(name) VALUES ('Фэнтези');

insert into author(firstname, lastname, patronymic)
VALUES ('Зыков', 'Виталий', 'Валерьевич');

insert into book(title, pages, description, author_id, genre_id)
values ('Ученик Своего Учителя. Родная Гавань', 448,
        'Малк вернулся домой в Борей. ' ||
             'Да, впереди ещё много работы, но завершение Большого Плана уже не за горами. ' ||
             'Он на финишной прямой и совсем скоро сможет оценить верность своих расчётов… ' ||
             'Вопрос в другом, понравится ли ему с таким трудом достигнутый результат ' ||
             'и не заведёт ли его тернистый путь борьбы за свою свободу куда-нибудь не туда?',
        1, 1
);

insert into book(title, pages, description, author_id, genre_id)
values ('Малк. Когда у тебя нет цели', 315,
        'Малк вернулся домой в Борей. ' ||
        'Эта история о мечтающем стать магом провинциальном парне, ' ||
        'который несмотря ни на что и вопреки всему отправляется покорять культурную столицу. ' ||
        'Однако вместо ожидаемой учебы и последующих забот о карьере он приобретает коварных врагов, ' ||
        'сталкивается с магами и демонами, с головой уходит в магические изыскания и… все больше и ' ||
        'больше запутывается в паутине чужих и весьма грязных интриг. ' ||
        'Ведь так просто играть с тем, кто просто живет и в чьей жизни нет цели.',
        1, 1
       );



insert into subscription(title, active, created, paid, identity_id)
VALUES ('admin-sub', true, '2004-10-19 10:23:54', true, 1);

insert into subscription_book(subscription_id, book_id)
values (1, 1);

insert into payment_type(name) values ('Наличные');
insert into payment_type(name) values ('Карта');

insert into payment(payment_type_id, subscription_id) values (2, 1);