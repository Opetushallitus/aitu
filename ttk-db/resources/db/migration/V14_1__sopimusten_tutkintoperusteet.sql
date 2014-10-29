-- kts. https://issues.solita.fi/browse/OPH-990
update sopimus_ja_tutkinto st set tutkintoversio = t2.tutkintoversio_id
from tutkintoversio t2 inner join tutkintoversio t1 on t2.versio = (t1.versio +1) and t1.peruste = t2.peruste and t1.tutkintotunnus = t2.tutkintotunnus
  where st.tutkintoversio = t1.tutkintoversio_id;
  