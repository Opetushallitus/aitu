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

(ns user
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :as nsr]
            [clojure.test :refer [test-ns]]
            [clj-http.client :as hc]
            stencil.loader
            clojure.core.cache
            schema.core))

(schema.core/set-fn-validation! true)

;; Templatejen kakutus pois päältä kehityksen aikana
(stencil.loader/set-cache (clojure.core.cache/ttl-cache-factory {} :ttl 0))

;;; Ks. http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded

(defonce ^:private palvelin (atom nil))

(defn ^:private kaynnista! []
  {:pre [(not @palvelin)]
   :post [@palvelin]}
  (require 'aitu.palvelin)
  (require 'aitu.asetukset)
  (reset! palvelin ((ns-resolve 'aitu.palvelin 'kaynnista!)
                     (assoc @(ns-resolve 'aitu.asetukset 'oletusasetukset)
                            :development-mode true))))

(defn ^:private sammuta! []
  {:pre [@palvelin]
   :post [(not @palvelin)]}
  ((ns-resolve 'aitu.palvelin 'sammuta) @palvelin)
  (reset! palvelin nil))

(defn uudelleenkaynnista! []
  (when @palvelin
    (sammuta!))
  (nsr/refresh :after 'user/kaynnista!))

(defmacro with-testikayttaja
  [& body]
  (require '[oph.korma.korma-auth :as ka])
  `(binding [(ns-resolve 'ka '*current-user-oid*) (ns-resolve 'ka 'default-test-user-oid)] ~@body))
