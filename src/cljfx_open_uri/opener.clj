(ns cljfx-open-uri.opener
  (:require [clojure.pprint :refer [pprint]]))

(def init {:mdl/uri nil})

(defmulti upset :evt/type)

(defmethod upset :evt/uri-value-changed
  [{:keys [coe/state fx/event] :as _evt-arg-map}]
  (let [new-uri-str event
        state-map state]
    {:eff/log ["URI value changed with event:" event]
     :eff/state (assoc state-map :mdl/uri new-uri-str)}))

(defmethod upset :evt/open-uri-btn-clicked
  [{:keys [coe/state] :as _evt-arg-map}]
  (let [uri-str (state :mdl/uri)]
    {:eff/log (format "Opening URI: %s" uri-str)
     :eff/open-uri uri-str}))

(defmethod upset :default
  [args]
  {:eff/log (str "Unknown event " (:evt/type args))})

(defn- log! [arg _dispatch!]
  (println arg))

(defn- open-uri! [uri-str _dispatch]
  (let [uri-obj (java.net.URI. uri-str)
        desktop (java.awt.Desktop/getDesktop)]
    (.browse desktop uri-obj)))

(def coeffects
  {})

(def effects
  {:eff/log #(log! %1 %2)
   :eff/open-uri #(open-uri! %1 %2)})

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
                       :text (state-map :mdl/uri)
                       :on-text-changed {:evt/type :evt/uri-value-changed}}
                      {:fx/type :button :text "Open URI" :on-action {:evt/type :evt/open-uri-btn-clicked}}
                      {:fx/type :text
                       :text (with-out-str (pprint state-map))}]}}})