(ns cljfx-open-uri.core
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache])
  (:gen-class))

(def init {:mdl/iconified false})

(defmulti upset :evt/type)

(defmethod upset :evt/log-btn-clicked
  [_args]
  {:eff/log "Greetings!"})

(defmethod upset :evt/iconify-btn-clicked
  [{:keys [fx/context] :as evt-arg-map}]
  {:eff/log ["Iconify button clicked with args:" evt-arg-map]
   :context (fx/swap-context context assoc :mdl/iconified true)})

(defmethod upset :default
  [args]
  {:eff/log (str "Unknown event " (:evt/type args))})

(defn- log! [arg _dispatch!]
  (println arg))

(def effects
  {:eff/log log!})

(defn view [state-map]
  {:fx/type :stage
   :showing true
   :iconified (state-map :mdl/iconified)
   :scene
   {:fx/type :scene
    :root {:fx/type :v-box
           :children [{:fx/type :text
                       :text "Hello 🙂"}
                      {:fx/type :button
                       :text "Log greetings"
                       :on-action {:evt/type :evt/log-btn-clicked}}
                      ;; TODO: 1. implement iconify button
                      ;; TODO: 2. implement text input for URI
                      ;; TODO: 3. implement 'Open URI' button
                      {:fx/type :button
                       :text "Do something unexpected"
                       :on-action {:evt/type :evt/unexpected-btn-clicked}}
                      {:fx/type :button
                       :text "Iconify window"
                       :on-action {:evt/type :evt/iconify-btn-clicked}}]}}})

;; TODO: 99. separate `app` and `runtime` namespaces

;; TODO: 99. generalize this function as `actual-view` or `wrap-pure-view`
(defn view-context [{:keys [fx/context]}]
  (let [state-map (fx/sub-val context identity)]
    (view state-map)))

(def *context
  (atom (fx/create-context init cache/lru-cache-factory)))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(def app
  (fx/create-app *context
                 :event-handler upset
                 :effects effects
                 :desc-fn (fn [_]
                            {:fx/type view-context})))

(defn raise-window! []
  (swap! *context fx/swap-context assoc :mdl/iconified true)
  (future
    (Thread/sleep 100)
    (swap! *context fx/swap-context assoc :mdl/iconified false)))

(comment
  (raise-window!))