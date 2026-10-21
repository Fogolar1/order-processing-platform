FROM maven:3.9.9-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml pom.xml
COPY order-service/pom.xml order-service/pom.xml
COPY inventory-service/pom.xml inventory-service/pom.xml
COPY payment-service/pom.xml payment-service/pom.xml

COPY order-service/src order-service/src
COPY inventory-service/src inventory-service/src
COPY payment-service/src payment-service/src

RUN mvn -pl inventory-service -am -DskipTests package

RUN find /workspace/inventory-service/target -maxdepth 1 -name '*.jar' -print

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /workspace/inventory-service/target/*.jar app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=75.0", "-jar", "/app/app.jar"]
