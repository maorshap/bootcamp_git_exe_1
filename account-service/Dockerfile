FROM openjdk:8-alpine

COPY account-service/build/libs/ /usr/app/

COPY account-service/src/main/resources/ /usr/app

COPY infrastructure/src/main/resources/ /usr/app

WORKDIR /usr/app

RUN mv account-service-1.0-SNAPSHOT-all.jar account-service.jar

EXPOSE 8090

CMD ["java", "-jar", "account-service.jar"]