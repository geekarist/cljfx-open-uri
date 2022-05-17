(ns cljfx-open-uri.core
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]
            [cljfx-open-uri.opener :as opener])
  (:gen-class))

(def *context
  (atom (fx/create-context opener/init cache/lru-cache-factory)))

(defn set-state! [state-map _dispatch!]
  (swap! *context fx/reset-context state-map))

(defn view-context [{:keys [fx/context]} view-fn]
  (let [state-map (fx/sub-val context identity)]
    (view-fn state-map)))

(def app
  (fx/create-app *context
                 :event-handler opener/upset
                 :co-effects (assoc opener/coeffects
                                    :coe/state #(fx/sub-val (deref *context) identity))
                 :effects (assoc opener/effects
                                 :eff/state #(set-state! %1 %2))
                 :desc-fn (fn [_]
                            {:fx/type #(view-context % opener/view)})))

(defn apply-changes! []
  (println "Applying changes")
  (let [renderer (app :renderer)]
    (renderer)))

(apply-changes!)