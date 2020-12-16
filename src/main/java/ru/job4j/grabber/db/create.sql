create database grabber;

create table post
(
    id serial PRIMARY KEY,
    name varchar(2083) NOT NULL,
    text text NOT NULL,
    link varchar(2083) UNIQUE NOT NULL,
    created varchar(255) NOT NULL
);