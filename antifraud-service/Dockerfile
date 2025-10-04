FROM amazoncorretto:21-alpine
VOLUME /tmp
ARG JAR_FILE=target/antifraud-service-1.0.0-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
