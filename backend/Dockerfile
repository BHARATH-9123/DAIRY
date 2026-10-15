# Build the Spring Boot application with the Java version declared in pom.xml.
FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml ./
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package -DskipTests

# Render supplies PORT at runtime; Spring Boot reads it through application.properties.
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
COPY --from=build /app/target/m-dairy-server-1.0.0.jar app.jar

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
