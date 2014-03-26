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

(ns aitu.infra.i18n-test
  (:import java.util.Locale)
  (:require [clojure.test :refer [deftest testing is are]]
            [aitu.infra.i18n :refer :all]))

(deftest kielikoodi-ja-uri-test
  (are [uri tulos] (= (kielikoodi-ja-uri {:uri uri}) tulos)
    "/" [nil "/"]
    "/ei/kielikoodia" [nil "/ei/kielikoodia"]
    "/sv/foo/bar" ["sv" "/foo/bar"]
    "/fi/foo/bar" ["fi" "/foo/bar"]))

(deftest accept-languagen-kielikoodi-test
  (are [accept-language tulos]
    (let [kysely {:headers {"accept-language" accept-language}}]
      (= (accept-languagen-kielikoodi kysely) tulos))
    "" nil
    ;; Tuetaan vain suomea ja ruotsia
    "en" nil
    "fi" "fi"
    "sv" "sv"
    ;; Valitaan ensimmäinen tuettu kieli
    "en fi sv" "fi"
    "en sv fi" "sv"
    ;; Ei välitetä "quality valuesta"
    "fi;q=0.6 sv;q=1" "fi"))

(deftest wrap-locale-test
  (testing "wrap-locale"
    (testing "Poistaa kielikoodin URI:sta"
      (is (= ((wrap-locale identity)
               {:uri "/fi/foo/bar"
                :headers {}})
             {:uri "/foo/bar"
              :headers {}})))

    (testing "Asettaa *locale*:n URI:n kielikoodin perusteella"
      (is (= ((wrap-locale (fn [_] *locale*))
               {:uri "/fi/foo/bar"
                :headers {"accept-language" "sv"}})
             (Locale. "fi"))))

    (testing "Asettaa *locale*:n Accept-Languagen perusteella, jos URI:n kielikoodi puuttuu"
      (is (= ((wrap-locale (fn [_] *locale*)) {:uri "/foo/bar"
                                               :headers {"accept-language" "sv"}})
             (Locale. "sv"))))

    (testing "Tekee redirectin suomenkieliseen URI:in, jos URI:n kielikoodi puuttuu, eikä Accept-Languagessa ole tuettua kieltä"
      (is (= ((wrap-locale (fn [_] :vastaus))
               {:uri "/foo/bar"
                :headers {"accept-language" "en"}})
             {:status 302
              :headers {"Location" "/fi/foo/bar"}
              :body ""})))

    (testing "Ei tee redirectiä URI:lle, joiden osalta se on estetty"
      (is (= ((wrap-locale (fn [_] :vastaus) :ei-redirectia #"/foo/.*")
               {:uri "/foo/bar"
                :headers {}})
             :vastaus)))

    (testing "Asettaa localen myös URI:lle, joiden osalta redirect on estetty"
      (is (= ((wrap-locale (fn [_] *locale*) :ei-redirectia #"/foo/.*")
               {:uri "/foo/bar"
                :headers {"accept-language" "sv"}})
             (Locale. "sv"))))

    (testing "Jos kielikoodi puuttuu ja redirect on estetty, *locale* jää unbound-tilaan"
      (is (= ((wrap-locale (fn [_] (bound? #'*locale*)) :ei-redirectia #"/foo/.*")
               {:uri "/foo/bar"
                :headers {}})
             false)))))
