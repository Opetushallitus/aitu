(ns ttk.selain)

(defn CHelloController [$scope]
  (set! (.-hello $scope) "Hello World from ClojureScript and AngularJS"))

(def ^:export HelloController
  (array
    "$scope"
    CHelloController))
