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

(ns aitu-e2e.util
  (:import com.paulhammant.ngwebdriver.ByAngular
           com.paulhammant.ngwebdriver.WaitForAngularRequestsToFinish
           java.util.concurrent.TimeUnit
           (org.openqa.selenium.remote CapabilityType
                                       DesiredCapabilities
                                       RemoteWebDriver)
           (org.openqa.selenium UnexpectedAlertBehaviour
                                NoAlertPresentException
                                UnhandledAlertException)
           org.openqa.selenium.firefox.FirefoxDriver
           org.openqa.selenium.TimeoutException)
  (:require [clojure.test :refer [is]]
            [clj-webdriver.taxi :as w]
            [clj-webdriver.driver :refer [init-driver]]))

(def ^:dynamic *ng*)
(def default-user "T-1001")

(defmacro odota-kunnes [& body]
  `(w/wait-until (fn [] ~@body) 20000))

(defn odota-sivun-latautumista []
  (let [ready-state (atom nil)]
    (try
      (odota-kunnes (= (reset! ready-state
                               (w/execute-script "return document.readyState"))
                       "complete"))
      (catch TimeoutException e
        (println (str "document.readyState == '" @ready-state "'"))
        (throw e)))))

(defn odota-angular-pyyntoa []
  (odota-sivun-latautumista)
  (WaitForAngularRequestsToFinish/waitForAngularRequestsToFinish
    (:webdriver w/*driver*)))

(defn luo-webdriver! []
  (let [remote_url (System/getenv "REMOTE_URL")
        browser-name (or (System/getenv "BROWSER_NAME") "internet explorer")
        capabilities (doto
                       (if remote_url
                         (doto (DesiredCapabilities.) (.setBrowserName browser-name))
                         (DesiredCapabilities.))
                       (.setCapability
                         CapabilityType/UNEXPECTED_ALERT_BEHAVIOUR
                         UnexpectedAlertBehaviour/IGNORE))
        driver (init-driver
                 (if remote_url
                    (RemoteWebDriver. (java.net.URL. remote_url) capabilities)
                    (FirefoxDriver. capabilities)))]
    (w/set-driver! driver)
    (-> driver :webdriver .manage .timeouts (.setScriptTimeout 30 TimeUnit/SECONDS))))

(defn puhdista-selain []
  (w/to "about:blank")
  (odota-sivun-latautumista))

(defn tarkista-js-virheet [f]
  (let [tulos (f)
        js-virheet (try
                     (w/execute-script "return window.jsErrors")
                     (catch UnhandledAlertException _
                       (-> w/*driver* :webdriver .switchTo .alert .accept)
                       (w/execute-script "return window.jsErrors")))]
    (is (empty? js-virheet))
    (w/execute-script "window.jsErrors = []")
    tulos))

(defn tarkista-otsikkotekstit [tulos]
  (let [tarkistettavat-elementit ["h1" "h2" "h3" "label" "th" ".table-header .table-cell"]
        tarkistukset (for [elementti tarkistettavat-elementit]
                       {:elementti elementti :tyhjia (count (filter w/displayed? (w/find-elements {:css (str elementti ":empty:not(.select2-offscreen)")})))})]

    (is (every? #(= (:tyhjia %) 0) tarkistukset)))
  tulos)

(defn with-webdriver* [f]
  (if (bound? #'*ng*)
    (do
      (try
        (puhdista-selain)
        (catch UnhandledAlertException _
          (-> w/*driver* :webdriver .switchTo .alert .accept)))
      (-> (tarkista-js-virheet f)
          (tarkista-otsikkotekstit)))
    (do
      (luo-webdriver!)
      (try
        (binding [*ng* (ByAngular. (:webdriver w/*driver*))]
          (-> (tarkista-js-virheet f)
              (tarkista-otsikkotekstit)))
        (finally
          (w/quit))))))

(defmacro with-webdriver [& body]
  `(with-webdriver* (fn [] ~@body)))

(defn aitu-url [polku]
  (str (or (System/getenv "AITU_URL")
           "http://192.168.50.1:8080")
       polku))

(defn cas-url []
  (or (System/getenv "CAS_URL")
      "https://localhost:9443/cas-server-webapp-3.5.2"))

(defn cas-kirjautuminen [kayttaja]
  (w/quick-fill-submit {"#username" kayttaja}
                       {"#password" kayttaja}
                       {"#password" w/submit}))

(defn cas-uloskirjautuminen []
  (let [logout-url (str (cas-url) "/logout")]
    (w/to logout-url)
    (try
      (odota-kunnes (= (w/current-url) logout-url))
      (catch TimeoutException e
        (println (str "Odotettiin selaimen siirtyvän CAS logout -sivulle, mutta url oli '" (w/current-url) "'"))
        (throw e)))))

(defn avaa
  ([polku] (avaa aitu-url polku default-user))
  ([osoite-fn polku] (avaa osoite-fn polku default-user))
  ([osoite-fn polku kayttaja]
    (let [url (osoite-fn polku)
          cas-url (cas-url)]
      (w/to url)
      (try
        (odota-kunnes (or (= (w/current-url) url)
                          (re-find (re-pattern cas-url) (w/current-url))))
        (catch TimeoutException e
          (println (str "Odotettiin selaimen siirtyvän URLiin '" url "' tai '" cas-url "'"
                        ", mutta sen URL oli '" (w/current-url) "'"))
          (throw e)))
      (when (not= (w/current-url) url)
        (cas-kirjautuminen kayttaja)
        (avaa osoite-fn polku kayttaja))
      (odota-angular-pyyntoa))))

(defn avaa-kayttajana* [polku kayttaja f]
  (cas-uloskirjautuminen)
  (avaa aitu-url polku kayttaja)
  (f)
  (cas-uloskirjautuminen))

(defmacro avaa-kayttajana [polku kayttaja & body]
  `(avaa-kayttajana* ~polku ~kayttaja (fn [] ~@body)))

(defn avaa-uudelleenladaten [polku]
  (puhdista-selain)
  (avaa polku))

(defn sivun-otsikko []
  (w/text "h1"))

(defn aseta-inputtiin-arvo-jquery-selektorilla [selektori arvo]
  (w/execute-script (str selektori ".val('" arvo "').trigger('input').trigger('change')")))

(defn tyhjenna-input [ng-model-nimi]
  (aseta-inputtiin-arvo-jquery-selektorilla (str "$('input[ng-model=\"" ng-model-nimi "\"]')") ""))

(defn tyhjenna-datepicker-input [valittu-pvm-model]
  (aseta-inputtiin-arvo-jquery-selektorilla (str "$('fieldset[valittu-pvm=\"" valittu-pvm-model "\"] input[type=\"text\"]')") ""))

(defn elementin-teksti [binding-name]
  (w/text (w/find-element (-> *ng*
                            (.binding binding-name)))))

(defn enum-elementin-teksti [nimi]
  (w/text {:css (str "span[nimi=" nimi "]:not([class*='ng-hide'])")}))

(defn viestin-teksti [] (some-> (w/find-element {:css ".api-method-feedback p.message"})  w/text))

(defn viestit-virheellisista-kentista []
  (vec (map w/text (w/find-elements (-> *ng*
                                     (.repeater "virhe in palaute.virheet"))))))

(defn elementilla-luokka? [elementti luokka]
  (->
    elementti
    (w/attribute "class")
    (.contains luokka)))

(defn tallenna []
  (w/click "button[ng-click=\"tallenna()\"]")
  (odota-angular-pyyntoa))

(defn tallennus-nappi-aktiivinen? []
  (w/enabled? (w/find-element {:css "button[ng-click=\"tallenna()\"]"})))

(defn pakollinen-kentta? [label-text]
  (let [elementti (first (filter w/displayed? (w/find-elements {:text label-text :tag "label"})))]
    (some->
      elementti
      (w/attribute "class")
      (.indexOf "pakollinen")
      (> -1))))

(defn valitse-select2-optio
  "Valitsee ensimmäisen option hakuehto listalta"
  [malli tunnistekentta hakuehto ]
  (let [select2-container-selector (str "fieldset"
                                        "[model=\"" malli "\"]"
                                        "[model-id-property=\"" tunnistekentta "\"]"
                                        " div.select2-container")]
    (w/execute-script (str "$('" select2-container-selector "').data('select2').open()")))
  (odota-kunnes (-> (w/find-elements {:css "#select2-drop input.select2-input"}) (count) (> 0)))
  (w/clear "#select2-drop input")
  (w/input-text "#select2-drop input" hakuehto)
  (odota-kunnes (-> (w/find-elements {:css "#select2-drop input.select2-active"}) (count) (= 0)))
  (w/click "#select2-drop .select2-results li:first-child"))

(defn syota-kenttaan [ng-model-nimi arvo]
  (tyhjenna-input ng-model-nimi)
  (w/input-text (str "input[ng-model=\"" ng-model-nimi "\"]") arvo))

(defn syota-pvm [ng-model-nimi pvm]
  (let [selector (str "fieldset[valittu-pvm=\"" ng-model-nimi "\"] input[type=\"text\"]")]
    (w/clear selector)
    (w/input-text selector pvm)))

(defn dialogi-nakyvissa? [teksti-re]
  (try
    (let [alert (-> w/*driver* :webdriver .switchTo .alert)]
      (boolean (re-find teksti-re (.getText alert))))
    (catch NoAlertPresentException _
      false)))

(defn peruutan-dialogin []
  (-> w/*driver* :webdriver .switchTo .alert .dismiss)
  (odota-angular-pyyntoa))

(defn hyvaksy-dialogi []
  (-> w/*driver* :webdriver .switchTo .alert .accept)
  (odota-angular-pyyntoa))

(def hyvaksyn-dialogin hyvaksy-dialogi)

(defn lista [lista-nimi]
  (into []
        (for [elements (w/find-elements {:css (str lista-nimi " tbody tr")})]
          (clojure.string/split (w/text elements) #"\n"))))

(defn listarivi [lista-nimi rivi]
  (clojure.string/join " " ((lista lista-nimi) rivi)))

(defn valitse-radiobutton [ng-model-nimi arvo]
  (let [selector (str "input[ng-model=\"" ng-model-nimi "\"][value=\"" arvo "\"]")]
    (w/click selector)))

(defn kirjoita-tekstialueelle [ng-model-nimi arvo]
  (aseta-inputtiin-arvo-jquery-selektorilla (str "$('textarea[ng-model=\"" ng-model-nimi "\"]')") arvo))

(defn hae-teksti-jquery-selektorilla
  [selektori]
  (some->
    (w/execute-script (str "return " selektori ".get(0)"))
    (.getText)))

(defn klikkaa-linkkia [teksti]
  (w/click {:tag :a :text teksti})
  (odota-angular-pyyntoa))

(defn odota-dialogia [teksti-re]
  (odota-kunnes (dialogi-nakyvissa? teksti-re)))

(defn tallenna-ja-hyvaksy-dialogi []
  ;; Ei voida käyttää tallenna-funktiota, koska Angularin odottaminen vaatii
  ;; JavaScriptin suorittamista, mikä ei onnistu, kun dialogi on auki.
  (w/click "button[ng-click=\"tallenna()\"]")
  (odota-dialogia #"")
  (hyvaksy-dialogi))
