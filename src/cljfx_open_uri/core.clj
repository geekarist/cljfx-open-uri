(ns cljfx-open-uri.core
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache])
  (:gen-class))

(def init {:mdl/iconified false})

(defmulti upset :evt/type)

(defmethod upset :evt/log-btn-clicked
  [_args]
  {:eff/log "Greetings!"})

(defmethod upset :default
  [args]
  {:eff/log (str "Unknown event " (:evt/type args))})

(defn- log! [arg _dispatch!]
  (println arg))

(def effects
  {:eff/log log!})

(defn view [{:keys [fx/context]}]
  (let [iconified? (fx/sub-val context :mdl/iconified)]
    {:fx/type :stage
     :showing true
     :iconified iconified?
     :scene
     {:fx/type :scene
      :root {:fx/type :v-box
             :children [{:fx/type :text
                         :text "Hello 🙂"}
                        {:fx/type :button
                         :text "Log greetings"
                         :on-action {:evt/type :evt/log-btn-clicked}}
                        {:fx/type :button
                         :text "Do something unexpected"
                         :on-action {:evt/type :evt/unexpected-btn-clicked}}]}}}))

(def *context
  (atom (fx/create-context init cache/lru-cache-factory)))

(def actual-handler
  (-> upset
      (fx/wrap-effects effects)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def app
  (fx/create-app *context
                 :event-handler actual-handler
                 :desc-fn (fn [_]
                            {:fx/type view})))

(defn raise-window! []
  (swap! *context fx/swap-context assoc :mdl/iconified true)
  (future
    (Thread/sleep 100)
    (swap! *context fx/swap-context assoc :mdl/iconified false)))

(comment
  (raise-window!))