drop table if exists otp cascade;

alter table users add column otp boolean;

update users set otp = false;