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
