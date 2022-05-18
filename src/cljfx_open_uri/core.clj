(ns cljfx-open-uri.core
  (:require [cljfx-open-uri.runtime :as runtime]
            [cljfx-open-uri.opener :as opener])
  (:gen-class))

(def app
  (runtime/create!
   opener/init
   (fn [] opener/view) ; Makes it possible to reload the fn
   opener/upset
   opener/coeffects
   opener/effects))

(defn apply-changes! []
  (println "Applying changes")
  (runtime/apply-changes! app))

(apply-changes!)