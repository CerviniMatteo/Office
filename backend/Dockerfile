# ---- Build stage ----
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copia solo il pom.xml prima per sfruttare la cache dei layer Docker
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia il resto del codice sorgente e builda
COPY src ./src
RUN mvn clean package -DskipTests -B

# ---- Runtime stage ----
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

ENV SPRING_PROFILES_ACTIVE=test

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]