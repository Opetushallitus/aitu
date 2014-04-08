(ns aitu.integraatio.sql.jarjestamissopimus-auth-test
  (:require [aitu.compojure-util :as cu]
            [clojure.test :refer [deftest testing is are use-fixtures]]
            [aitu.integraatio.sql.test-data-util :refer :all]
            [aitu.integraatio.sql.test-util :refer :all]
            [aitu.toimiala.kayttajaoikeudet :refer :all]))

(use-fixtures :each tietokanta-fixture)

(defn autorisoi-sopimuksen-paivitys [konteksti]
  (cu/autorisoi :sopimustiedot_paivitys konteksti true))

(defn autorisoi-sopimuksen-luku [konteksti]
  (cu/autorisoi :sopimustiedot_luku konteksti true))

(defn autorisoi-suunnitelman-luku [konteksti]
  (cu/autorisoi :suunnitelma_luku konteksti true))

(defn autorisoi-liitteen-luku [konteksti]
  (cu/autorisoi :sopimuksen_liite_luku konteksti true))

(defn operaatio-onnistuu [autorisointi-fn konteksti]
  (is (autorisointi-fn konteksti)))

(defn operaatio-ei-onnistu [autorisointi-fn konteksti]
  (is (thrown? Throwable
        (autorisointi-fn konteksti))))

(deftest ^:integraatio sopimuksen-paivitys-onnistuu-toimikunnan-jasenelta []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-onnistuu autorisoi-sopimuksen-paivitys "1")))

(deftest  ^:integraatio sopimuksen-paivitys-ei-onnistu-jos-ei-toimikunnan-jasen []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-ei-onnistu autorisoi-sopimuksen-paivitys "2")))

(deftest  ^:integraatio sopimuksen-luku-onnistuu-toimikunnan-jasenelta []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-onnistuu autorisoi-sopimuksen-luku "1")))

(deftest  ^:integraatio sopimuksen-luku-ei-onnistu-jos-ei-toimikunnan-jasen []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-ei-onnistu autorisoi-sopimuksen-luku "2")))

(deftest  ^:integraatio suunnitelman-luku-onnistuu-toimikunnan-jasenelta []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-onnistuu autorisoi-suunnitelman-luku "1")))

(deftest  ^:integraatio suunnitelman-luku-ei-onnistu-jos-ei-toimikunnan-jasen []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-ei-onnistu autorisoi-suunnitelman-luku "2")))

(deftest  ^:integraatio liitteen-luku-onnistuu-toimikunnan-jasenelta []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-onnistuu autorisoi-liitteen-luku "1")))

(deftest  ^:integraatio suunnitelman-luku-ei-onnistu-jos-ei-toimikunnan-jasen []
  (lisaa-jarjestamissopimus!)
  (with-user-rights
    #(operaatio-ei-onnistu autorisoi-liitteen-luku "2")))