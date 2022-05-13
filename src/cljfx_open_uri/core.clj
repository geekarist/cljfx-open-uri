(ns cljfx-open-uri.core
  (:require [cljfx.api :as fx]
            [clojure.core.cache :as cache]
            [clojure.pprint :refer [pprint]])
  (:gen-class))

(def init {:mdl/uri nil})

(defmulti upset :evt/type)

(defmethod upset :evt/uri-value-changed
  [{:keys [fx/context fx/event] :as _evt-arg-map}]
  (let [text-str event]
    {:eff/log ["URI value changed with event:" event]
     :context (fx/swap-context context assoc :mdl/uri text-str)}))

(defmethod upset :evt/open-uri-btn-clicked
  [{:keys [fx/context] :as _evt-arg-map}]
  (let [uri-str (fx/sub-val context :mdl/uri)]
    {:eff/log (format "Opening URI: %s" uri-str)
     :eff/open-uri uri-str}))

(defmethod upset :default
  [args]
  {:eff/log (str "Unknown event " (:evt/type args))})

(defn- log! [arg _dispatch!]
  (println arg))

(defn- open-uri! [uri-str _dispatch]
  (let [uri-obj (java.net.URI. (format "%s#hello" uri-str))
        desktop (java.awt.Desktop/getDesktop)]
    (.browse desktop uri-obj)))

(def effects
  {:eff/log log!
   :eff/open-uri open-uri!})

(defn view [state-map]
  {:fx/type :stage
   :showing true
   :iconified false
   :scene
   {:fx/type :scene
    :root {:fx/type :v-box
           :children [{:fx/type :text
                       :text "Hello 🙂"}
                      {:fx/type :text-field
                       :prompt-text "Provide an URI"
                       :on-text-changed {:evt/type :evt/uri-value-changed}}
                      {:fx/type :button :text "Open URI" :on-action {:evt/type :evt/open-uri-btn-clicked}}
                      {:fx/type :text
                       :text (with-out-str (pprint state-map))}]}}})

(defn view-context [{:keys [fx/context]}]
  (let [state-map (fx/sub-val context identity)]
    (view state-map)))

(def *context
  (atom (fx/create-context init cache/lru-cache-factory)))

(def app
  (fx/create-app *context
                 :event-handler upset
                 :effects effects
                 :desc-fn (fn [_]
                            {:fx/type view-context})))

(defn apply-changes! []
  (let [renderer (app :renderer)]
    (renderer)))
