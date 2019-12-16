
FROM openjdk:8-alpine

COPY producer/build/libs/ /usr/app/

COPY producer/src/main/resources/ /usr/app

COPY infrastructure/src/main/resources/ /usr/app

WORKDIR /usr/app

RUN mv producer-1.0-SNAPSHOT-all.jar producer.jar

EXPOSE 8080

CMD ["java", "-jar", "producer.jar"]

