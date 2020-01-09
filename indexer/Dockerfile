FROM java:8-jdk-alpine

COPY indexer/build/libs/ /usr/app/

COPY indexer/src/main/resources/ /usr/app

COPY infrastructure/src/main/resources/ /usr/app

WORKDIR /usr/app

RUN mv Indexer-1.0-SNAPSHOT-all.jar indexer.jar

CMD ["java", "-jar", "indexer.jar"]