# Bygg med Maven
FROM maven:3.9.9-amazoncorretto-21-alpine AS build

WORKDIR /usr/src/app

# Kopier pom.xml først, slik at avhengighetene bare lastes ned på nytt når
# pom.xml faktisk endrer seg.
COPY ./pom.xml ./
RUN mvn -B dependency:go-offline

COPY ./src ./src/

RUN mvn -B package -DskipTests

# Kjør på en JRE. Merk: openjdk-imagene er avviklet, derfor Temurin.
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /usr/src/app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
