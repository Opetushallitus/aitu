alter table suorittaja
  drop constraint oidhetu_constraint,
  add constraint hetu_uniq unique(hetu),
  add constraint oid_uniq unique(oid),
  add CONSTRAINT oidhetu_set CHECK ((hetu is not null) or (oid is not null));
