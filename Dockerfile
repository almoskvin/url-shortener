FROM openjdk:8-jdk-alpine
MAINTAINER sil.moskvin@gmail.com
VOLUME /tmp
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-Dspring.data.mongodb.host=url-shortener-mongo","-jar","/app.jar"]
