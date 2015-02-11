create sequence tkunta_seq;
alter table tutkintotoimikunta alter column tkunta set default nextval('tkunta_seq')::varchar;
