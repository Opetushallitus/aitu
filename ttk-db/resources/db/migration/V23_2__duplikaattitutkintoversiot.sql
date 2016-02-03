delete from tutkintoversio tv1 where tv1.peruste = '68/011/2015' and exists (select 1 from tutkintoversio tv2 where tv2.peruste = tv1.peruste and tv2.tutkintoversio_id > tv1.tutkintoversio_id);
delete from tutkintoversio tv1 where tv1.peruste = '46/011/2006' and exists (select 1 from tutkintoversio tv2 where tv2.peruste = tv1.peruste and tv2.tutkintoversio_id < tv1.tutkintoversio_id);

insert into eperusteet_log default values;