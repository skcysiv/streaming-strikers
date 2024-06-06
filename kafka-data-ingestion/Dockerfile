FROM gcr.io/serapis-public-images/eclipse-temurin:17.0.3_7-jdk-focal
WORKDIR /app
COPY target/kafka-data-ingestion-1.0-SNAPSHOT.jar /app/kafka-data-ingestion-1.0-SNAPSHOT.jar
EXPOSE 8181
CMD ["java", "-jar", "kafka-data-ingestion-1.0-SNAPSHOT.jar"]
