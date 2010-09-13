(ns mobile.core
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

(def devices (re-pattern
              (str
               "palm|blackberry|nokia|phone|midp|mobi|symbian|chtml|ericsson|minimo|"
               "audiovox|motorola|samsung|telit|upg1|windows ce|ucweb|astel|plucker|"
               "x320|x240|j2me|sgh|portable|sprint|docomo|kddi|softbank|android|mmp|"
               "pdxgw|netfront|xiino|vodafone|portalmmm|sagem|mot-|sie-|ipod|up\\.b|"
               "webos|amoi|novarra|cdm|alcatel|pocket|ipad|iphone|mobileexplorer|"
               "mobile")))

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


