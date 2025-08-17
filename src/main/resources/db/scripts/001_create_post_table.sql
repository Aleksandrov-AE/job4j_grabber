create table post (
    id serial primary key,
    title text not null,
    link text unique not null,
    description text,
    created timestamp without time zone
);
