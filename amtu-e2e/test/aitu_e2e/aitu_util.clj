(ns aitu-e2e.aitu-util
  (:require [clj-webdriver.taxi :as w]
            [aitu-e2e.util :refer :all]))

(defn onko-tallenna-nappi-enabloitu? [napin-teksti]
  (some-> (filter w/visible? (w/find-elements {:text napin-teksti})) first w/enabled?))

(defn avaa
  ([polku]
    (avaa-url (aitu-url polku)))
  ([polku kayttaja]
    (avaa-url (aitu-url polku) kayttaja)))

(defn avaa-kayttajana* [polku kayttaja f]
  (avaa-url-kayttajana* (aitu-url polku) kayttaja f))

(defmacro avaa-kayttajana [polku kayttaja & body]
  `(avaa-kayttajana* ~polku ~kayttaja (fn [] ~@body)))

(defn avaa-uudelleenladaten [polku]
  (avaa-url-uudelleenladaten (aitu-url polku)))
