alter table products
add column description varchar(255);

update products set description = 'Temporary description';