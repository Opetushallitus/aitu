alter table suorittaja
  drop constraint oidhetu_constraint,
  add CONSTRAINT oidhetu_set CHECK ((hetu <> '') or (oid <> ''));

create unique index hetu_uniq on suorittaja(hetu) where hetu <> '';
create unique index oid_uniq on suorittaja(oid) where oid <> '';

