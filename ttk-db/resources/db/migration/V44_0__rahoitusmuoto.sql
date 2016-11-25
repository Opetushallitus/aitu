alter table suorittaja
  alter column rahoitusmuoto drop not null;
  
comment on table rahoitusmuoto is 'Poistuva tieto. Tämä taulu tullaan ehkä poistamaan kokonaan.';