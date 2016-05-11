alter table sopimus_ja_tutkinto
add column alkupvm date,
add column loppupvm date;

update sopimus_ja_tutkinto set alkupvm = js.alkupvm, loppupvm = js.loppupvm
from jarjestamissopimus js where js.jarjestamissopimusid = sopimus_ja_tutkinto.jarjestamissopimusid;