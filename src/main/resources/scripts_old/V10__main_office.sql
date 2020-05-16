alter table businesses add column main_office_id bigint;

update businesses set main_office_id = null;