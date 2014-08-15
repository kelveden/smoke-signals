# smoke-signals

Notifier for [Campfire](http://campfirenow.com) messages. Polls the specified Campfire room every 10 seconds for new messages that match a specified pattern. If any messages match, will shell out to [notify-send](http://www.unix.com/man-page/OpenSolaris/1/notify-send/) with a message indicating how many messages matched 

So as long as your notification daemon uses [libnotify](https://developer.gnome.org/libnotify/) (as most do) it should pick up the notifications.

## Why?

In busier Campfire rooms it can be difficult to keep track of discussions that are relevant. smoke-signals tries to reduce the noise by only alerting you to the messages that match specific pattern(s). 

## Usage

Use Leiningen:

	lein run "http://your/campfire/room/url" "your-campfire-token" "pattern"
	
Or you can create an uberjar and run using java:

	lein uberjar
	java -jar path/to/uberjar "http://your/campfire/room/url" "your-campfire-token" "pattern"

## Using a HTTP proxy

Experimental: The app will automatically use whatever proxy settings are specified in the $http_proxy environment variable.
