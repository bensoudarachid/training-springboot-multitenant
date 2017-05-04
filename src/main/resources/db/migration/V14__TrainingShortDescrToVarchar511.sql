ALTER TABLE training DROP COLUMN short_description;
alter table training add column short_description varchar(511);
