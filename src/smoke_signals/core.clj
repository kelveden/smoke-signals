(ns smoke-signals.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.contrib.shell-out :as shell :only [sh]]))

(defn- get-latest-messages [campfire-url token]
  (((client/get (str campfire-url "/recent.json")
                 {:basic-auth [token "dummypassword"] :as :json})
    :body) :messages)
)

(defn- message-body [message]
  (let [body (message :body)]
    (if (nil? body) "" body)))

(defn- is-text-message? [message]
  (= (message :type) "TextMessage"))

(defn- filter-messages [messages pattern]
  (filter #(and 
            (is-text-message? %)
            (not (empty? (re-seq
                          (re-pattern pattern)
                          (message-body %))))) 
          messages))

(defn- notify-about [messages]
  (do 
    (shell/sh "notify-send" (str (count messages) " messages"))))

(defn -main [campfire-url token pattern]
  (notify-about
   (filter-messages (get-latest-messages campfire-url token) pattern)))
