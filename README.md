# SQS-MicroService

This is the MicroService course project, the application I built here read from routing config file, and keep polling from AWS SQS service, 
when there's messages exist in the queue, it gets the message, parse the header, figure out which MicroService to be calling, 
and form a RESTful URL request to the that API, wating for the response and parse it again, then put it into the response queue to be read
by the user.
