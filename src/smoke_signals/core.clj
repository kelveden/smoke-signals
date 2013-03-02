(ns smoke-signals.core
  (:gen-class)
  (:require [clj-http.client :as client]
            [clojure.contrib.shell-out :as shell :only [sh]]
            [environ.core :as environ])
  (:import [java.util.concurrent TimeUnit]))

(def proxy-settings
  (let [proxy-env-matches (re-matches #"http://([^:].+):(.+)" (or (environ/env :http-proxy) ""))]
    {:proxy-host (nth proxy-env-matches 1)
     :proxy-port (Integer. (or (nth proxy-env-matches 2) 0))}))

(def most-recent-message-id
  "Atom storing the id of the most recent message from the last pulled
batch of Campfire messages. Used as a starting point for retrieving
the next batch."
  (atom nil))

(defn- get-latest-messages
  "Pulls the latest messages from Campfire that have been posted since
  the most recent message from the previously pulled batch."
  [campfire-url token]
  (-> (client/get (str campfire-url "/recent.json")
                  {:proxy-host (proxy-settings :proxy-host)
                   :proxy-port (proxy-settings :proxy-port)
                   :query-params {"since_message_id" @most-recent-message-id}
                   :basic-auth [token "dummypassword"]
                   :as :json})
      :body
      :messages))

(defn- text-message?
  "Returns true if the specified Campfire message is of type TextMessage."
  [message]
  (= (message :type) "TextMessage"))

(defn- filter-messages
  "Filters out only those Campfire messages that have a body that
  matches the specified pattern."
  [messages pattern]
  (filter #(and 
            (text-message? %)
            (re-find (re-pattern pattern) (% :body ""))) 
          messages))

(defn- notify-about
  "Shells out to notify-send to send a notification of the count of the specified messages."
  [messages] 
  (when (seq messages)
    (shell/sh "notify-send" (str "Detected " (count messages) " Campfire messages"))))

(defn- store-most-recent-message-id
  "Stores the id of the most recent Campfire message to an atom"
  [messages]
  (when (seq messages)
    (reset! most-recent-message-id ((last messages) :id)))
  messages)

(defn -main
  "Polls the room specified by the given full Campfire room URL every
10 seconds for text messages that have a body that matches the
specified regex. If 1 or matches is found a message indicating the
number of matches is displayed to the user via shelling out to
notify-send."
  [campfire-url token pattern]
  (while true
    (-> (get-latest-messages campfire-url token)
        (store-most-recent-message-id)
        (filter-messages pattern)
        (notify-about))
    (.sleep TimeUnit/SECONDS 10)))
