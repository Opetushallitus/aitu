alter table jasenyys
  add constraint status_esitetty_vaatii_esittajan check (status <> 'esitetty' or esittaja is not null);
