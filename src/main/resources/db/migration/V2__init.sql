drop sequence if exists hibernate_sequence;
drop table if exists logs cascade;
drop table if exists user_roles cascade;
drop table if exists roles cascade;
drop table if exists cash_registers cascade;
drop table if exists office_inventories cascade;
drop table if exists office_profiles cascade;
drop table if exists offices cascade;
drop table if exists products cascade;
drop table if exists businesses cascade;
drop table if exists discounts cascade;
drop table if exists employee_profiles cascade;
drop table if exists contact_information cascade;
drop table if exists questions cascade;
drop table if exists answers cascade;
drop table if exists users cascade;
drop table if exists question_authors cascade;

create sequence if not exists hibernate_sequence MINVALUE 50;

create table if not exists logs
(
    id         bigint    not null
        constraint logs_pkey
            primary key,
    created_at timestamp not null,
    updated_at timestamp not null,
    message    varchar(255)
);

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
            unique
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


create table if not exists businesses
(
    id                 bigint    not null
        constraint businesses_pkey
            primary key,
    created_at         timestamp not null,
    updated_at         timestamp not null,
    name               varchar(255),
    restaurant_feature boolean   not null,
    merchant_id        bigint
);


create table if not exists contact_information
(
    id           bigint not null
        constraint contact_information_pkey
            primary key,
    address      varchar(255),
    city         varchar(255),
    country      varchar(255),
    email        varchar(255),
    phone_number varchar(255)
);


create table if not exists employee_profiles
(
    id              bigint    not null
        constraint employee_profiles_pkey
            primary key,
    created_at      timestamp not null,
    updated_at      timestamp not null,
    name            varchar(255),
    surname         varchar(255),
    account_id      bigint
        constraint fkh1dkqpo9xxa1lwlfkg1qv0nlu
            references users,
    business_id     bigint
        constraint fka8e6q6hirh4n4x2rm7y2tcncs
            references businesses,
    contact_info_id bigint null
        constraint fk7jrp663by977139cmjyt9gqn1
            references contact_information
);

alter table businesses
    add constraint fknqndphlt4tpyy6n52k9ddxp7t
        foreign key (merchant_id) references employee_profiles;


create table if not exists offices
(
    id              bigint    not null
        constraint offices_pkey
            primary key,
    created_at      timestamp not null,
    updated_at      timestamp not null,
    business_id     bigint    not null
        constraint fkbp1p3tusy5bhen3hkcn8cb9m6
            references businesses,
    contact_info_id bigint
        constraint fkmrb8ysjycec6ielfnm521fhwy
            references contact_information,
    manager_id      bigint
        constraint fk9xfh83raua1ehm5wvdnho2qrg
            references employee_profiles
);


create table if not exists office_profiles
(
    id          bigint    not null
        constraint office_profiles_pkey
            primary key,
    created_at  timestamp not null,
    updated_at  timestamp not null,
    employee_id bigint
        constraint fklq2nvgsjnd3q4a7fcw3e570is
            references employee_profiles,
    office_id   bigint
        constraint fkhabcxlwsc5s3203u6vpx7l3pi
            references offices
);

-- roles must be added like this
INSERT INTO public.roles (id, name) VALUES (1, 'ROLE_ADMIN') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (2, 'ROLE_MANAGER') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (3, 'ROLE_MERCHANT') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (4, 'ROLE_WAREMAN') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (5, 'ROLE_PRW') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (6, 'ROLE_CASHIER') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (7, 'ROLE_BARTENDER') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;
INSERT INTO public.roles (id, name) VALUES (8, 'ROLE_OFFICEMAN') ON CONFLICT ON CONSTRAINT roles_pkey DO NOTHING;

-- we must create admin user
INSERT INTO public.users (id, created_at, updated_at, email, password, username)
VALUES (1, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'root@gmail.com',
'$2a$10$uKBANDuasXdXGsJ7O1AAyuieHpwMGXQ191AgRRRzSFa9bKDab9S4e', 'root') ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;

-- we must give admin role to this user
INSERT INTO public.user_roles (user_id, role_id) VALUES (1, 1) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;

INSERT INTO public.businesses (id, created_at, updated_at, name, restaurant_feature, merchant_id)
VALUES (1, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'BINGO', false, null);

INSERT INTO public.users (id, created_at, updated_at, email, password, username)
VALUES (2, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'business1@gmail.com',
        '$2a$10$uKBANDuasXdXGsJ7O1AAyuieHpwMGXQ191AgRRRzSFa9bKDab9S4e', 'business1') ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;

INSERT INTO public.user_roles (user_id, role_id) VALUES (2, 3) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;
INSERT INTO public.user_roles (user_id, role_id) VALUES (2, 2) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;

INSERT INTO public.contact_information (id, address, city, country, email, phone_number)
VALUES (2, 'some address', 'Sarajevo', 'Bosnia', 'business1@gmail.com', '+38733222111');

INSERT INTO public.employee_profiles (id, created_at, updated_at, name, surname, account_id, business_id,
                                      contact_info_id)
VALUES (2, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'Bman', 'SBman', 2, 1, 2);

UPDATE public.businesses
SET merchant_id=2
WHERE id = 1;

INSERT INTO public.users (id, created_at, updated_at, email, password, username)
VALUES (3, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'office1@gmail.com',
        '$2a$10$uKBANDuasXdXGsJ7O1AAyuieHpwMGXQ191AgRRRzSFa9bKDab9S4e', 'office1') ON CONFLICT ON CONSTRAINT users_pkey DO NOTHING;

INSERT INTO public.user_roles (user_id, role_id) VALUES (3, 8) ON CONFLICT ON CONSTRAINT user_roles_pkey DO NOTHING;

INSERT INTO public.contact_information (id, address, city, country, email, phone_number)
VALUES (3, 'some office address', 'Sarajevo', 'Bosnia', 'office1@gmail.com', '+38733222112');

INSERT INTO public.employee_profiles (id, created_at, updated_at, name, surname, account_id, business_id,
                                      contact_info_id)
VALUES (3, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 'OFFMan', 'SOFFMan', 3, 1, 3);

INSERT INTO public.offices (id, created_at, updated_at, business_id, contact_info_id, manager_id)
VALUES (1,'2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000',1,3,3);

INSERT INTO public.office_profiles (id, created_at, updated_at, employee_id, office_id)
VALUES (1, '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000', 3, 1);











