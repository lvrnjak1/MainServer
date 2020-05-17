alter table pdv_rates add column active boolean;

update pdv_rates set active = true;