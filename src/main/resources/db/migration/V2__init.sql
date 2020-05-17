drop sequence if exists hibernate_sequence;
drop table if exists user_roles cascade;
drop table if exists roles cascade;
drop table if exists users cascade;
drop table if exists receipt_statuses cascade;
drop table if exists pdv_rates cascade;
drop table if exists payment_methods cascade;
drop table if exists reservation_statuses cascade;

create sequence if not exists hibernate_sequence MINVALUE 50;

create table if not exists roles
(
    id   bigint not null
        constraint roles_pkey
            primary key,
    name varchar(60)
        constraint uk_nb4h0p6txrmfc0xbrd1kglp9t
            unique
);

create table if not exists users
(
    id         bigint    not null
        constraint users_pkey
            primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    email      varchar(255)
        constraint uk6dotkott2kjsp8vw4d0m25fb7
            unique,
    password   varchar(255),
    username   varchar(255)
        constraint ukr43af9ap4edm43mmtq01oddj6
            unique,
    otp        boolean
);

create table if not exists user_roles
(
    user_id bigint not null
        constraint fkhfh9dx7w3ubf1co1vdev94g3f
            references users,
    role_id bigint not null
        constraint fkh8ciramu9cc9q3qcqiv4ue8a6
            references roles,
    constraint user_roles_pkey
        primary key (user_id, role_id)
);

create table if not exists receipt_statuses
(
    id          bigint    not null
        constraint receipt_statuses_pkey
            primary key,
    created_at  timestamp not null,
    updated_at  timestamp not null,
    status_name varchar(60)
        constraint uk_kb9lhqfcds25c61t5aog9f6as
            unique
);

create table if not exists reservation_statuses
(
    id   bigint not null
        constraint reservation_statuses_pkey
            primary key,
    name varchar(60)
        constraint uk_4lujeqo57jenmii5ytlp180vo
            unique
);

create table if not exists payment_methods
(
    id          bigint    not null
        constraint payment_methods_pkey
            primary key,
    created_at  timestamp not null,
    updated_at  timestamp not null,
    method_name varchar(60)
        constraint uk_mrp7rc4u5dyjw9xqqhf6koxd
            unique
);

create table if not exists pdv_rates
(
    id       bigint           not null
        constraint pdv_rates_pkey
            primary key,
    pdv_rate double precision not null,
    active   boolean
);

--create user roles
INSERT INTO public.roles (id, name) VALUES (1, 'ROLE_ADMIN')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (2, 'ROLE_MANAGER')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (3, 'ROLE_MERCHANT')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (4, 'ROLE_WAREMAN')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (5, 'ROLE_PRW')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (6, 'ROLE_CASHIER')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (7, 'ROLE_BARTENDER')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (8, 'ROLE_OFFICEMAN')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (9, 'ROLE_PRP')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (10, 'ROLE_SERVER')
ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;

--create receipt statuses
INSERT INTO public.receipt_statuses (id, status_name, created_at, updated_at)
VALUES (1, 'PAID', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT receipt_statuses_pkey DO NOTHING;

INSERT INTO public.receipt_statuses (id, status_name, created_at, updated_at)
VALUES (2, 'CANCELED', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT receipt_statuses_pkey DO NOTHING;

INSERT INTO public.receipt_statuses (id, status_name, created_at, updated_at)
VALUES (3, 'PENDING', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT receipt_statuses_pkey DO NOTHING;

INSERT INTO public.receipt_statuses (id, status_name, created_at, updated_at)
VALUES (4, 'INSUFFICIENT_FUNDS', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT receipt_statuses_pkey DO NOTHING;

INSERT INTO public.receipt_statuses (id, status_name, created_at, updated_at)
VALUES (5, 'DELETED', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT receipt_statuses_pkey DO NOTHING;

--create payment methods
INSERT INTO public.payment_methods (id, method_name, created_at, updated_at)
VALUES (1, 'CASH', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT payment_methods_pkey DO NOTHING;
INSERT INTO public.payment_methods (id, method_name, created_at, updated_at)
VALUES (2, 'CREDIT_CARD', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT payment_methods_pkey DO NOTHING;
INSERT INTO public.payment_methods (id, method_name, created_at, updated_at)
VALUES (3, 'PAY_APP', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT payment_methods_pkey DO NOTHING;

--create reservation statuses
insert into public.reservation_statuses(id, name) values(1, 'VERIFIED')
ON CONFLICT ON CONSTRAINT reservation_statuses_pkey DO NOTHING;
insert into public.reservation_statuses(id, name) values(2, 'UNVERIFIED')
ON CONFLICT ON CONSTRAINT reservation_statuses_pkey DO NOTHING;
insert into public.reservation_statuses(id, name) values(3, 'CANCELED')
ON CONFLICT ON CONSTRAINT reservation_statuses_pkey DO NOTHING;
insert into public.reservation_statuses(id, name) values(4, 'DONE')
ON CONFLICT ON CONSTRAINT reservation_statuses_pkey DO NOTHING;

--create pdv rates
INSERT INTO public.pdv_rates (id, pdv_rate, active) VALUES (1, 17, true)
ON CONFLICT ON CONSTRAINT pdv_rates_pkey DO NOTHING;
INSERT INTO public.pdv_rates (id, pdv_rate, active) VALUES (2, 0, true)
ON CONFLICT ON CONSTRAINT pdv_rates_pkey DO NOTHING;

--create root user
INSERT INTO public.users (id, created_at, updated_at, email, password, username, otp)
VALUES (1, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'root@gmail.com',
'$2a$10$uKBANDuasXdXGsJ7O1AAyuieHpwMGXQ191AgRRRzSFa9bKDab9S4e', 'root', false )
ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;

--add admin role to root user
INSERT INTO public.user_roles (user_id, role_id) VALUES (1, 1)
ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;
