alter table cash_registers
add column uuid varchar(255);

update cash_registers set uuid = '7a3fbcdc-79b0-11ea-bc55-0242ac130003';