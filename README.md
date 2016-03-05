# hessian_server
This is a server for Hello world with Hessian protocol

# How do I run this server?
* step 1: make a war file
````
mvn clean
mvn package
````
The war file ("server.war") is generated in the "target/".

* step 2: copy war file to the server where to run it

````cp server.war /opt/jetty/webapps/````

* step 3: run jetty (version 9.3.7) on a Ubuntu server (version : 14.04)
````
sudo service jetty restart
````
