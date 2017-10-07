create table event (id bigint not null, end datetime, number smallint not null, start datetime, version integer, training_id bigint, primary key (id));
alter table event add constraint FK98y3w8n6hupioh2e8owayymm1 foreign key (training_id) references training (id);
