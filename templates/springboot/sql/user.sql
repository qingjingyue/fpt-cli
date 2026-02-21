create database if not exists spring_boot_template;
use spring_boot_template;

create table if not exists user
(
    id          bigint auto_increment primary key,
    username    varchar(50)  not null unique,
    password    varchar(50)  null,
    email       varchar(50)  null unique,
    phone       varchar(11)  null unique,
    avatar      varchar(255) null,
    create_time datetime default current_timestamp,
    update_time datetime default current_timestamp on update current_timestamp
)
