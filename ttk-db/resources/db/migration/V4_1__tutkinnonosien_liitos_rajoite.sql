alter table tutkinto_ja_tutkinnonosa
  add constraint tutkinto_not_null check (tutkinto is not null);

alter table tutkinto_ja_tutkinnonosa
  add constraint tutkinnonosa_not_null check (tutkinnonosa is not null);

