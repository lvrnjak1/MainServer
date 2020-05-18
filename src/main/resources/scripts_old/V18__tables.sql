drop table if exists tables cascade;
drop table if exists reservations cascade;

create table tables
(
    id           bigint  not null
        constraint tables_pkey
            primary key,
    table_name_in_office varchar(255) not null,
    office_id    bigint
        constraint fk4uacm27lq94lberru8isy97rr
            references offices
);

alter table businesses add column place_name varchar(255) ;

update businesses set place_name = 'Tables';


