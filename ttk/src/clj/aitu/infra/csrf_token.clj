(ns aitu.infra.csrf-token
  (:import java.util.Random
           java.util.UUID)
  (:require
    [clojure.tools.logging :as log]
    [aitu.toimiala.kayttajaoikeudet :as ko]))

;;Double submit cookies: https://www.owasp.org/index.php/Cross-Site_Request_Forgery_%28CSRF%29_Prevention_Cheat_Sheet#Double_Submit_Cookies

(defn tarkasta-csrf-token [ring-handler]
  (fn [request]
    (let [xsrf-cookie-value (get-in request [:cookies "XSRF-TOKEN" :value])
          xsrf-header-value (get-in request [:headers "x-xsrf-token"])]
      (if
          (and
            xsrf-cookie-value
            xsrf-header-value
            (.equals xsrf-header-value xsrf-cookie-value))
        (ring-handler request)
        (do
          (log/error "Virheellinen XSRF-TOKEN.")
          {:status 401})))))

(defn aseta-csrf-token []
  (let [random (java.util.Random.)
        token (java.util.UUID. (.nextLong random) (.nextLong random))] ;;Käytetään randomia, jotta entropian loppuminen ei aiheuta ongelmia.
    (str "XSRF-TOKEN=" token "; Path=/;")))