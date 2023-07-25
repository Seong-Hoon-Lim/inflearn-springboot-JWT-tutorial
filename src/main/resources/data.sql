-- DROP ALL OBJECTS;
-- DROP TABLE `member`;
-- DROP TABLE `authority`;
-- DROP TABLE if exists users CASCADE;
-- USER 테이블을 먼저 생성합니다.
-- CREATE TABLE IF NOT EXISTS `member` (
--     `member_id` BIGINT AUTO_INCREMENT PRIMARY KEY,
--     `member_name` VARCHAR(50) UNIQUE NOT NULL,
--     `password` VARCHAR(100) NOT NULL,
--     `nick_name` VARCHAR(50),
--     `activated` VARCHAR(1) NOT NULL
-- );

CREATE TABLE IF NOT EXISTS `authority` (
    `authority_name` VARCHAR(50) NOT NULL
);

-- 이후 나머지 데이터를 삽입합니다.
insert into `member` (`member_name`, `password`, `nick_name`, `role`, `activated`) values ('admin', '$2a$08$lDnHPz7eUkSi6ao14Twuau08mzhWrL4kyZGGU5xfiGALO/Vxd5DOi', 'admin', 'ROLE_MEMBER', 1);
insert into `member` (`member_name`, `password`, `nick_name`, `role`, `activated`) values ('member', '$2a$08$UkVvwpULis18S19S5pZFn.YHPZt3oaqHZnDwqbCW9pft6uFtkXKDC', 'member', 'ROLE_ADMIN', 1);

insert into `authority` (`authority_name`) values ('ROLE_MEMBER');
insert into `authority` (`authority_name`) values ('ROLE_ADMIN');

insert into member_authority (`member_id`, `authority_name`) values (1, 'ROLE_MEMBER');
insert into member_authority (`member_id`, `authority_name`) values (1, 'ROLE_ADMIN');