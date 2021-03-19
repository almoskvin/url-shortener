FROM openjdk:8-jdk-alpine
MAINTAINER sil.moskvin@gmail.com
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.data.mongodb.host=url-shortener-mongo","-jar","/app.jar"]
