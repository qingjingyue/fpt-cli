create database if not exists example;

use example;

create table if not exists user
(
    id          bigint auto_increment primary key comment '用户ID',
    username    varchar(26)  not null unique comment '用户名',
    password    varchar(80)  null comment '密码',
    email       varchar(50)  null unique comment '邮箱',
    phone       varchar(11)  null unique comment '手机号',
    avatar      varchar(255) null comment '头像',
    create_time datetime default current_timestamp comment '创建时间',
    update_time datetime default current_timestamp on update current_timestamp comment '更新时间'
) comment '用户表';
