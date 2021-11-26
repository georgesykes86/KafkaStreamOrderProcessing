# KafKa Streaming Demo for Order Processing

## Summary

This is a demonstration repo for how Kafka and Kafka Streams can be used to process orders. The primary brief is as follows.

There are a number of domain objects. 
* Products - Id, Name, Description
* Prices
* Order - have Product Id and quantity

Using Kafka and Kafka Streams API we want to create producers for all these objects and consumers which can meet the following requirements.
* Creat detailed orders by joining products and prices wit the order events 
* Prices can be updated and description can be updated at any time
* Ensure order details are correct based on time of event
* Should be packaged as a single application
  
Things that you might want to consider to achieve the objectives are
- Stream Processing - KStream KTable
- Windows, collapsing

## Getting started

To start the kafka cluster ``docker-compose up -d``

Stop all docker containers ``docker-compose down``
