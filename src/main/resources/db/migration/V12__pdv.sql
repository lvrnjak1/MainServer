alter table products add column pdv float;
alter table receipt_items add column pdv float;

update products set pdv = 17;
update receipt_items set pdv = 17;

create table pdv_rates
(
    id       bigint           not null
        constraint pdv_rates_pkey
            primary key,
    pdv_rate double precision not null
);

INSERT INTO public.pdv_rates (id, pdv_rate) VALUES (94, 17);
