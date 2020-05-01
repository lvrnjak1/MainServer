create table if not exists reservation_statuses
(
    id   bigint not null
        constraint reservation_statuses_pkey
            primary key,
    name varchar(60)
        constraint uk_4lujeqo57jenmii5ytlp180vo
            unique
);

insert into reservation_statuses(id, name) values(1, 'VERIFIED');
insert into reservation_statuses(id, name) values(2, 'UNVERIFIED');
insert into reservation_statuses(id, name) values(3, 'CANCELED');
insert into reservation_statuses(id, name) values(4, 'DONE');