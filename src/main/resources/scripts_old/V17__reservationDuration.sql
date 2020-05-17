alter table businesses add column reservation_duration bigint ;

update businesses set reservation_duration = 60;