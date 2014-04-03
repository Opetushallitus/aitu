(ns aitu-e2e.aitu-util
  (:require [clj-webdriver.taxi :as w]))

(defn onko-tallenna-nappi-enabloitu? [napin-teksti]
  (some-> (filter w/visible? (w/find-elements {:text napin-teksti})) first w/enabled?))


