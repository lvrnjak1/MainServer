alter table businesses add column max_number_offices integer;
alter table offices add column max_number_cash_registers integer;

update businesses set max_number_offices = 5;
update offices set max_number_cash_registers = 5;