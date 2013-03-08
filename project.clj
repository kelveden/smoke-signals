(defproject smoke-signals "0.1.0-SNAPSHOT"
  :description "Triggers notification based on the messages from a Campfire room."
  :url "http://github.com/kelveden/smoke-signals"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses"}
  :dependencies [[clj-http "0.6.4"],
                 [org.clojure/clojure "1.4.0"]
                 [org.clojure/clojure-contrib "1.2.0"]
		 [environ "0.3.0"]]
  :main smoke-signals.core)
