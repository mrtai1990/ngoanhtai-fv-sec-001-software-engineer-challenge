FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN apt-get update && apt-get install -y maven && mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

COPY --from=builder /build/target/ad-performance-aggregator-1.0.0.jar app.jar

ENTRYPOINT ["java", "-Xms256m", "-Xmx1024m", "-XX:+UseG1GC", "-jar", "app.jar"]