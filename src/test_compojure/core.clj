(ns test-compojure.core
  (:use compojure.core
        ring.adapter.jetty
        hiccup.core)
  (:require [compojure.route :as route]
            [clojure.contrib.str-utils2 :as str2]))


(defn regex-union [regexes]
  (let [str-regexes (map str regexes)
        joined (interpose "|" str-regexes)]
    (apply str joined)))

(defn get-user-agent [request]
  (let [headers (:headers request)
        user-agent (headers "user-agent")]
    user-agent))

(defn matches-mobile [request mobile-regex]
  (let [user-agent (get-user-agent request)]
    (if (re-seq mobile-regex (str2/lower-case user-agent)) true false)))

(def devices (re-pattern "iphone|android"))

(defn handle-mobile [your-func]
  (fn
    [handler]
    (fn [request]
      (let [response (handler request)
            current-body (:body response)]
        (assoc response :body
               (if (matches-mobile request devices)
                 (your-func current-body)
                 current-body))))))

(defn greeting [x] (html [:html
                          [:h1 "You are on mobile!"] [:div x]]))

(defroutes myapp
  (GET "/" [] (html [:h1 "Hello World!"]))
  (GET "/header" request (html [:p "Hello World!"]) )
  (route/not-found "Page not found"))

(wrap! myapp
          ((handle-mobile greeting)))

(run-jetty myapp {:port 8080})
