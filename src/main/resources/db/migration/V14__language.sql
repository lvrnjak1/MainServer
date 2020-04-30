alter table offices add column language_name varchar(60) ;

update offices set language_name = 'ENGLISH';
