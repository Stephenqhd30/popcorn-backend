# 数据库初始化
# @author stephen qiu
#

-- 创建库
create database if not exists popcorn;

-- 切换库
use popcorn;

-- 用户表
create table user
(
    id           bigint auto_increment comment 'id'
        primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userName     varchar(256)                           null comment '用户昵称',
    userGender   int          default 2                 null comment '用户性别（0-男 ，1-女，2-保密）',
    userPhone    varchar(256)                           null comment '手机号码',
    userAvatar   varchar(1024)                          null comment '用户头像',
    userProfile  varchar(512)                           null comment '用户简介',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    userEmail    varchar(256)                           null comment '用户邮箱',
    tags         varchar(1024)                          null comment '标签列表',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
)
    comment '用户' collate = utf8mb4_unicode_ci;

create table tag
(
    id         bigint auto_increment comment 'id'
        primary key,
    tagName    varchar(256)                       not null comment '标签名称',
    userId     bigint                             not null comment '用户id',
    parentId   bigint                             null comment '父标签id',
    isParent   tinyint  default 0                 null comment '0-不是父标签，1-是父标签',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '标签表';

-- 队伍表
create table team
(
    id           bigint auto_increment comment 'id'
        primary key,
    teamName     varchar(256)                       not null comment '队伍名称',
    teamProfile  varchar(1024)                      null comment '队伍简介',
    coverImage   varchar(256)                       null comment '队伍图标',
    expireTime   datetime                           null comment '队伍过期时间',
    userId       bigint                             not null comment '创建人id',
    status       tinyint                            not null comment '队伍状态（0-公开,1-私密,2-需要密码）',
    teamPassword varchar(256)                       null comment '队伍密码',
    createTime   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete     tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍表';

-- 队伍-用户表
create table team_user
(
    id         bigint auto_increment comment 'id'
        primary key,
    userId     bigint                             not null comment '用户id',
    teamId     bigint                             not null comment '队伍id',
    joinTime   datetime default CURRENT_TIMESTAMP not null comment '加入时间',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除'
)
    comment '队伍-用户表';