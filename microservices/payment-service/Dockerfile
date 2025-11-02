FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=build /app/target/payment-service-0.0.1-SNAPSHOT.jar payment.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","payment.jar"]