-- OPH-1960, OPH-1962
update tutkintoversio
  set siirtymaajan_loppupvm = voimassa_loppupvm where
  siirtymaajan_loppupvm < voimassa_loppupvm;
  
