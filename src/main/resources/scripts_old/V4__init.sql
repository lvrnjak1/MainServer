drop table if exists inventory_log cascade;
drop table if exists inventory_logs cascade;

create table if not exists inventory_logs
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