# Multi-stage build para Java 21 / Spring Boot 4
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Copia arquivos do Maven Wrapper e pom.xml
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x mvnw

# Copia código-fonte e compila
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Imagem de execucao leve com JRE 21
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /app/target/backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE 8080

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
