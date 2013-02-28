(ns smoke-signals.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.contrib.shell-out :as shell :only [sh]])
  (:import [java.util.concurrent TimeUnit]))

(def most-recent-message-id (atom nil))

;; Pulls the latest messages from Campfire that have been posted since the most recent
;; message from the previously pulled batch.
(defn- get-latest-messages [campfire-url token]
  (-> (client/get (str campfire-url "/recent.json")
               {:query-params {"since_message_id" @most-recent-message-id}
                :basic-auth [token "dummypassword"]
                :as :json})
      :body
      :messages))

(defn- text-message? [message]
  (= (message :type) "TextMessage"))

;; Filters out only those Campfire messages that have a body that matches the specified pattern.
(defn- filter-messages [messages pattern]
  (filter #(and 
            (text-message? %)
            (re-find (re-pattern pattern) (% :body ""))) 
          messages))

;; Shells out to notify-send to send a notification of the count of the specified messages.
(defn- notify-about [messages] 
  (when (seq messages)
    (shell/sh "notify-send" (str "Detected " (count messages) " Campfire messages"))))

;; Stores the id of the most recent Campfire message to an atom
(defn- store-most-recent-message-id [messages]
  (when (seq messages)
    (reset! most-recent-message-id ((last messages) :id)))
  messages)

(defn -main [campfire-url token pattern]
  (while true
    (-> (get-latest-messages campfire-url token)
        (store-most-recent-message-id)
        (filter-messages pattern)
        (notify-about))
    (.sleep TimeUnit/SECONDS 10)))
