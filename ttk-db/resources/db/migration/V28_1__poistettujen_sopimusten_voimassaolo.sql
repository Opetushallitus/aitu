update jarjestamissopimus set voimassa = false where poistettu;
alter table jarjestamissopimus add constraint jarjestamissopimus_poistettu_ei_voimassa check (not (voimassa and poistettu));