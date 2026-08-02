FROM maven:3.9.9-eclipse-temurin-22

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["mvn", "jpro:run"]