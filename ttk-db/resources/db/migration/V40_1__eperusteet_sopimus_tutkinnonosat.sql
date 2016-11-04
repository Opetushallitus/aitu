-- https://issues.solita.fi/browse/OPH-1882
-- ePerusteet on ristiriidassa Aitun tietokannan kanssa, joten poistetaan sopimuksista kaksi ongelmallista tutkinnonosaa.
-- nämä sopimukset pitää erikseen tietenkin korjata oikeiksi.

delete from sopimus_ja_tutkinto_ja_tutkinnonosa 
  where tutkinnonosa in (select tutkinnonosa_id from tutkinnonosa where osatunnus in ('101039', '101041'));
