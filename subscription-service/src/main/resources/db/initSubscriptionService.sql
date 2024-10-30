
create table if not exists subscription(
    id serial primary key ,
    identity_id serial unique references identity(id)
        on DELETE cascade on UPDATE cascade
);

insert into subscription(identity_id) values (1);