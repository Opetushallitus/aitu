;; Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
;;
;; This program is free software:  Licensed under the EUPL, Version 1.1 or - as
;; soon as they will be approved by the European Commission - subsequent versions
;; of the EUPL (the "Licence");
;;
;; You may not use this work except in compliance with the Licence.
;; You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
;;
;; This program is distributed in the hope that it will be useful,
;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;; European Union Public Licence for more details.

(ns aitu.integraatio.sql.validate-triggers  
  (:require
    [clojure.test :refer [deftest testing is]]
    [korma.core :as sql]
    [aitu.integraatio.sql.test-util :refer :all]))

(deftest ^:integraatio tarkista-puuttuvat-triggerit
  (testing "tarkistetaan että kaikilla omilla tauluilla on ainakin joku update/insert triggeri"
    (tietokanta-fixture
      #(let [flyway-taulu "schema_version"
            vialliset-taulut (sql/exec-raw
                               (str "select table_name from information_schema.tables" 
                                 " where not exists ("
                                 " select * from pg_class left outer join pg_trigger on tgrelid=pg_class.oid"
                                 " where tgtype in(7,19) and relname = table_name) "
                                 " and table_type='BASE TABLE' and table_schema='public' "
                                 " and table_name != '" flyway-taulu "'"
                                 " order by table_name") :results)]
        (when-not  (empty? vialliset-taulut)
          (println ".. ehkä haluaisit kirjoittaa jotain tällaista?")
          (doseq [taulu-map vialliset-taulut]
            (let [taulu (:table_name taulu-map)]
              (println (str "alter table " taulu " add column muutettu_kayttaja varchar(80) NOT NULL references kayttaja(oid);"))
              (println (str "alter table " taulu " add column luotu_kayttaja varchar(80) NOT NULL references kayttaja(oid);"))
              (println (str "alter table " taulu " add column muutettuaika timestamptz NOT NULL;"))
              (println (str "alter table " taulu " add column luotuaika timestamptz NOT NULL;"))
              (println (str "create trigger " taulu "_update before update on " taulu " for each row execute procedure update_stamp() ;"))
              (println (str "create trigger " taulu "l_insert before insert on " taulu " for each row execute procedure update_created() ;"))
              (println (str "create trigger " taulu "m_insert before insert on " taulu " for each row execute procedure update_stamp() ;"))
              (println (str "create trigger " taulu "_mu_update before update on " taulu " for each row execute procedure update_modifier() ;"))
              (println (str "create trigger " taulu "_mu_insert before insert on " taulu " for each row execute procedure update_modifier() ;"))
              (println (str "create trigger " taulu "_cu_insert before insert on " taulu " for each row execute procedure update_creator() ;")))))
        (is (empty? vialliset-taulut) (str "viallisia tauluja! " vialliset-taulut))))))

   