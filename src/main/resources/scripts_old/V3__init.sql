drop table if exists inventory_logs cascade;
drop table if exists warehouse_logs cascade;
drop table if exists payment_methods cascade;

create table if not exists inventory_log
(
    id         bigint           not null
        constraint inventory_log_pkey
            primary key,
    created_at timestamp        not null,
    updated_at timestamp        not null,
    quantity   double precision not null,
    office_id  bigint
        constraint fk4bffijqh3p962ayms8nrxohet
            references offices
            on delete cascade,
    product_id bigint
        constraint fka9lfrv77k71r2harv6b1al9ft
            references products
            on delete cascade
);

create table if not exists warehouse_logs
(
    id         bigint           not null
        constraint warehouse_logs_pkey
            primary key,
    created_at timestamp        not null,
    updated_at timestamp        not null,
    quantity   double precision not null,
    product_id bigint
        constraint fk25cdan1nrjmidnkpklkfaipxh
            references products
            on delete cascade
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

INSERT INTO public.receipt_statuses (id, status_name, created_at, updated_at)
VALUES (5, 'DELETED', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT receipt_statuses_pkey DO NOTHING;

INSERT INTO public.payment_methods (id, method_name, created_at, updated_at)
VALUES (1, 'CASH', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT payment_methods_pkey DO NOTHING;
INSERT INTO public.payment_methods (id, method_name, created_at, updated_at)
VALUES (2, 'CREDIT_CARD', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT payment_methods_pkey DO NOTHING;
INSERT INTO public.payment_methods (id, method_name, created_at, updated_at)
VALUES (3, 'PAY_APP', '2020-03-25 14:45:36.674000', '2020-03-25 14:45:36.674000')
ON CONFLICT ON CONSTRAINT payment_methods_pkey DO NOTHING;