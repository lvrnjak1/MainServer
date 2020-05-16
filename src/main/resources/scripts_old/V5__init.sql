drop table if exists receipt_items;
drop table if exists receipts;

create table if not exists reciepts
(
    id               bigint    not null
        constraint reciepts_pkey
            primary key,
    created_at       timestamp not null,
    updated_at       timestamp not null,
    business_id      bigint,
    cash_register_id bigint,
    office_id        bigint,
    receipt_id       varchar(255),
    timestamp        timestamp,
    total_price      numeric(19, 2),
    username         varchar(255),
    status_id        bigint
        constraint fkjynxt94q0u9k5dwolb6s0m509
            references receipt_statuses,
    method_id        bigint
        constraint fknlmxtpfashouxw2d8prhx465g
            references payment_methods
);

create table if not exists receipt_items
(
    id                  bigint           not null
        constraint receipt_items_pkey
            primary key,
    created_at          timestamp        not null,
    updated_at          timestamp        not null,
    barcode             varchar(255),
    discount_percentage integer          not null,
    price               numeric(19, 2),
    product_id          bigint,
    product_name        varchar(255),
    quantity            double precision not null,
    unit                varchar(255),
    receipt_id          bigint
        constraint fkpcwhf3s6w3ycn7yskfb3ack5r
            references reciepts
);