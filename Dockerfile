
FROM openjdk:8-alpine

COPY build/libs/ /usr/app/

COPY ./server.config /usr/app

WORKDIR /usr/app

RUN mv bootcamp_git_exe_1-1.0-SNAPSHOT-all.jar bootcamp.jar

EXPOSE 8080

CMD ["java", "-jar", "bootcamp.jar"]

