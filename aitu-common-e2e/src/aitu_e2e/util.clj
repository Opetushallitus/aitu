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
            [clojure.string :as string]
            clj-time.core
            clj-time.format
            clj-time.local
            [clj-webdriver.taxi :as w]
            [clj-webdriver.driver :refer [init-driver]]))

(def ^:dynamic *ng*)
(def default-user "T-1001")

(defmacro odota-kunnes [& body]
  `(w/wait-until (fn [] ~@body) 20000))

(def ^:dynamic *dialogit-kaytossa?* false)

(defmacro dialogit-kaytossa [& body]
  `(binding [*dialogit-kaytossa?* true]
     ~@body))

(declare dialogi-nakyvissa?)

(defn odota-sivun-latautumista []
  ;; OS X / FF 30 / WebDriver 2.42.2 -yhdistelmällä JavaScriptin suorittaminen
  ;; ei toimi, jos dialogi on näkyvissä. Oletetaan, että sivu on latautunut, jos
  ;; joku skripti on ehtinyt näyttää dialogin.
  ;;
  ;; Dialogien tarkastaminen on hidasta (> 2 s), joten tehdään se vain, jos
  ;; testataan dialogeja näyttävää koodia.
  (when (or (not *dialogit-kaytossa?*)
            (not (dialogi-nakyvissa?)))
    (let [ready-state (atom nil)]
      (try
        (odota-kunnes (= (reset! ready-state
                                 (w/execute-script "return document.readyState"))
                         "complete"))
        (catch TimeoutException e
          (println (str "document.readyState == '" @ready-state "'"))
          (throw e))))))

(defn odota-angular-pyyntoa []
  ;; OS X / FF 30 / WebDriver 2.42.2 -yhdistelmällä JavaScriptin suorittaminen
  ;; ei toimi, jos dialogi on näkyvissä. Oletetaan, että Angular on valmis, jos
  ;; joku skripti on ehtinyt näyttää dialogin.
  ;;
  ;; Dialogien tarkastaminen on hidasta (> 2 s), joten tehdään se vain, jos
  ;; testataan dialogeja näyttävää koodia.
  (when (or (not *dialogit-kaytossa?*)
            (not (dialogi-nakyvissa?)))
    (odota-sivun-latautumista)
    (WaitForAngularRequestsToFinish/waitForAngularRequestsToFinish
      (:webdriver w/*driver*))))

(defn luo-webdriver! []
  (let [remote_url (System/getenv "REMOTE_URL")
        browser-name (or (System/getenv "BROWSER_NAME") "internet explorer")
        capabilities (doto
                       (if remote_url
                         (doto (DesiredCapabilities.) (.setBrowserName browser-name))
                         (DesiredCapabilities.))
                       (.setCapability
                         CapabilityType/UNEXPECTED_ALERT_BEHAVIOUR
                         UnexpectedAlertBehaviour/IGNORE)
                       (.setCapability
                         CapabilityType/ACCEPT_SSL_CERTS
                         true))
        driver (init-driver
                 (if remote_url
                    (RemoteWebDriver. (java.net.URL. remote_url) capabilities)
                    (FirefoxDriver. capabilities)))]
    (w/set-driver! driver)
    (w/implicit-wait 3000)
    (-> driver :webdriver .manage .timeouts (.setScriptTimeout 30 TimeUnit/SECONDS))))

(defn puhdista-selain []
  ;; Siirrytään about:blank -sivulle kahdesti, koska ensimmäinen siirtymä
  ;; saattaa siirtymisen sijasta avata selaimen varmistusdialogin. Tämä tilanne
  ;; tunnistetaan siitä, että toinen siirtymä heittää UnhandledAlertExceptionin,
  ;; jolloin kuitataan dialogi, jotta siirtymä saadaan suoritettua loppuun.
  ;;
  ;; Toinen vaihtoehto olisi tarkistaa dialogin näkyvyys eksplisiittisesti ennen
  ;; ensimmäistä siirtymää, mutta tarkistus kestää > 2 s, joten tämä tapa on
  ;; nopeampi.
  (w/to "about:blank")
  (try
    (w/to "about:blank")
    (catch UnhandledAlertException _
      (-> w/*driver* :webdriver .switchTo .alert .accept)))
  (odota-sivun-latautumista))

(defn tarkasta-js-virheet [f]
  (let [tulos (f)
        _ (when *dialogit-kaytossa?*
            (try
              (-> w/*driver* :webdriver .switchTo .alert .dismiss)
              (catch NoAlertPresentException _)))
        js-virheet (w/execute-script "return window.jsErrors")]
    (is (empty? js-virheet))
    (w/execute-script "window.jsErrors = []")
    tulos))

(declare ota-kuva-tiedostoon)

(defn aja-ja-ota-kuva-epaonnistumisesta [f]
  (try
    (f)
    (catch Throwable e
      (ota-kuva-tiedostoon)
      (throw e))))

(defn yrita-puhdistaa-selain []
  (aja-ja-ota-kuva-epaonnistumisesta (fn [] puhdista-selain)))

(defn aja-testit-ja-tarkasta-virheet [f]
  (aja-ja-ota-kuva-epaonnistumisesta (fn [] (tarkasta-js-virheet f))))

(defn with-webdriver* [f]
  (if (bound? #'*ng*)
    (do
      (yrita-puhdistaa-selain)
      (aja-testit-ja-tarkasta-virheet f))
    (do
      (luo-webdriver!)
      (try
        (binding [*ng* (ByAngular. (:webdriver w/*driver*))]
          (aja-testit-ja-tarkasta-virheet f))
        (finally
          (w/quit))))))

(defmacro with-webdriver [& body]
  `(with-webdriver* (fn [] ~@body)))

(defn aitu-url [polku]
  (str (or (System/getenv "AITU_URL")
           "http://192.168.50.1:8080")
       polku))

(defn casissa? []
  (.startsWith (w/title) "CAS"))

(def cas-url (atom nil))

(defn cas-kirjautuminen [kayttaja]
  {:pre [(casissa?)]}
  (reset! cas-url (string/replace (w/current-url) #"(.*)/login.*" "$1"))
  (w/quick-fill-submit {"#username" kayttaja}
                       {"#password" kayttaja}
                       {"#password" w/submit}))

(defn cas-uloskirjautuminen []
  ;; cas-url asetetaan sisäänkirjautumisen yhteydessä. Jos ei olla kirjauduttu
  ;; sisään, ei tarvitse kirjautua uloskaan.
  (when @cas-url
    (let [logout-url (str @cas-url "/logout")]
      (w/to logout-url)
      (try
        (odota-kunnes (= (w/current-url) logout-url))
        (catch TimeoutException e
          (println (str "Odotettiin selaimen siirtyvän CAS logout -sivulle, mutta url oli '" (w/current-url) "'"))
          (throw e)))
      (reset! cas-url nil))))

(defn avaa-url
  ([url]
    (avaa-url url default-user))
  ([url kayttaja]
    (w/to url)
    (try
      (odota-kunnes (or (= (w/current-url) url) (casissa?)))
      (catch TimeoutException e
        (println (str "Odotettiin selaimen siirtyvän URLiin '" url "'"
                      ", mutta sen URL oli '" (w/current-url) "'"))
        (throw e)))
    (if (casissa?)
      (do
        (cas-kirjautuminen kayttaja)
        (recur url kayttaja))
      (odota-angular-pyyntoa))))

(defn avaa-url-kayttajana* [url kayttaja f]
  (cas-uloskirjautuminen)
  (avaa-url url kayttaja)
  (f)
  (cas-uloskirjautuminen))

(defn avaa-url-uudelleenladaten [url]
  (puhdista-selain)
  (avaa-url url))

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
  "Valitsee ensimmäisen option hakuvalitsimen listalta"
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

(defn valitse-puhdas-select2-optio
  "Valitsee ensimmäisen option select2-listalta"
  [malli hakuehto]
  (w/execute-script (str "$('select[ng-model=\"" malli "\"]').data('select2').open()"))
  (odota-kunnes (-> (w/find-elements {:css "#select2-drop input.select2-input"}) (count) (> 0)))
  (w/clear "#select2-drop input")
  (w/input-text "#select2-drop input" hakuehto)
  (odota-kunnes (-> (w/find-elements {:css "#select2-drop input.select2-active"}) (count) (= 0)))
  (w/click "#select2-drop .select2-results li.select2-result-selectable"))


(defn syota-kenttaan [ng-model-nimi arvo]
  (tyhjenna-input ng-model-nimi)
  (w/input-text (str "input[ng-model=\"" ng-model-nimi "\"]") arvo))

(defn syota-pvm [ng-model-nimi pvm]
  (let [selector (str "fieldset[valittu-pvm=\"" ng-model-nimi "\"] input[type=\"text\"]")]
    (w/clear selector)
    (w/input-text selector pvm)))

(defn dialogi-nakyvissa?
  ([]
    (dialogi-nakyvissa? nil))
  ([teksti-re]
    (try
      (let [alert (-> w/*driver* :webdriver .switchTo .alert)]
        (if teksti-re
          (boolean (re-find teksti-re (.getText alert)))
          true))
      (catch NoAlertPresentException _
        false))))

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

(defn luo-aikaleima-tiedostonimea-varten
  []
  (clj-time.format/unparse
    (.withZone (clj-time.format/formatter "yyyyMMdd'T'HHmmssSSS") (clj-time.core/default-time-zone))
    (clj-time.local/local-now)))

(defn ota-kuva-tiedostoon
  []
  (w/take-screenshot w/*driver* :file (str "screenshot-" (luo-aikaleima-tiedostonimea-varten) ".png")))
