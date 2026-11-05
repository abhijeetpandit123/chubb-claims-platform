FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /source
COPY pom.xml .
COPY src ./src
RUN mvn -B -q test package
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /source/target/claims-api-0.1.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
