# Read Me First
The following was discovered as part of building this project:

* The original package name 'com.example.spring-event-driven-microservices' is invalid and this project uses 'com.example.springeventdrivenmicroservices' instead.

# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.5/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.5/maven-plugin/reference/html/#build-image)

### Commands used 

* **docker compose:**

docker-compose % docker-compose -f common.yml -f kafka_cluster.yml up

* **After ensuring brokers and schema registery up and running, you can start application:**


* **kafkacat/kcat :**

docker run -it --network=host edenhill/kcat:1.7.1 kafkacat -L -b localhost:19092

* **to use kcat command on Local - Mac :**
  brew install kcat

* **List kafka brokers specified in host and port:**
  kcat -L -b localhost:19092

* **Check as a consumer the messages coming to the broker with specified topic:**
kcat -C -b localhost:19092 -t twitter-topic