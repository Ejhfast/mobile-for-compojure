(ns mobile.example
  (:use mobile.core
        compojure.core
        ring.adapter.jetty
        hiccup.core)
  (:require [compojure.route :as route]))

(defn greeting [x] (html [:html
                          [:h1 "You are on mobile!"] [:div x]]))

(defroutes myapp
  (GET "/" [] (html [:h1 "Hello World!"]))
  (route/not-found "Page not found"))

(wrap! myapp
          ((handle-mobile greeting)))

(run-jetty myapp {:port 8080})