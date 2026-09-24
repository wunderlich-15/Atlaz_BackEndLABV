FROM ubuntu:latest AS build

WORKDIR /app
RUN apt-get update
RUN apt-get install openjdk-17-jdk maven -y
COPY . .
RUN mvn clean install -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]