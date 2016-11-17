delete from tutkinnonosa
where tutkintoversio in (
  select tutkintoversio_id from tutkintoversio tv
  where peruste is null
  and voimassa_loppupvm > now()
  and not exists (select 1 from sopimus_ja_tutkinto where tutkintoversio = tv.tutkintoversio_id)
  and not exists (select 1 from suorituskerta where tutkintoversio_id = tv.tutkintoversio_id)
  and exists (select 1 from tutkintoversio tv2 where tv2.tutkintotunnus = tv.tutkintotunnus and tv2.tutkintoversio_id > tv.tutkintoversio_id)
);

delete from tutkintoversio tv
where peruste is null
and voimassa_loppupvm > now()
and not exists (select 1 from sopimus_ja_tutkinto where tutkintoversio = tv.tutkintoversio_id)
and not exists (select 1 from suorituskerta where tutkintoversio_id = tv.tutkintoversio_id)
and exists (select 1 from tutkintoversio tv2 where tv2.tutkintotunnus = tv.tutkintotunnus and tv2.tutkintoversio_id > tv.tutkintoversio_id);