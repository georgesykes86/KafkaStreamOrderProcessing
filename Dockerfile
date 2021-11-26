FROM openjdk:14-alpine
COPY build/libs/kafka-stream-order-processing-*-all.jar kafka-stream-order-processing.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "kafka-stream-order-processing.jar"]