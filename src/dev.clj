(ns dev
  (:require cljfx-open-uri.core))

#_{:clj-kondo/ignore [:clojure-lsp/unused-public-var]}
(defn apply-changes! []
  (cljfx-open-uri.core/raise-window!))