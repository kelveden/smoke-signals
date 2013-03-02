# smoke-signals

Notifier for [Campfire](http://campfirenow.com) messages. Polls the specified Campfire room every 10 seconds for new messages that match a specified pattern. If any messages match, will shell out to `notify-send` with a message indicating how many messages matched.

## Usage

Use Leiningen:

	lein run "http://your/campfire/room/url" "your-campfire-token" "pattern"
	
Or you can create an uberjar and run using java:

	lein uberjar
	java -jar path/to/uberjar "http://your/campfire/room/url" "your-campfire-token" "pattern"

## Using a HTTP proxy

Experimental: The app will automatically use whatever proxy settings are specified in the $http_proxy environment variable.
