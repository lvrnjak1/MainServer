alter table businesses add column sync_time time ;

update businesses set sync_time = '06:00:00';