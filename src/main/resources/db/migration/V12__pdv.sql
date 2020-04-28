alter table products add column pdv float;
alter table receipt_items add column pdv float;

update products set pdv = 17;
update receipt_items set pdv = 17;