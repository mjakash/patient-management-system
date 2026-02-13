FROM maven:3.9.11-eclipse-temurin-25-alpine AS builder

RUN apk add --no-cache libc6-compat gcompat

WORKDIR /app

#cache dependency
COPY pom.xml .
RUN mvn dependency:go-offline -B

#copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

#runner
FROM openjdk:25-ea-21-jdk-slim AS runner
WORKDIR /app


COPY --from=builder ./app/target/patient-service-0.0.1-SNAPSHOT.jar ./app.jar
EXPOSE 4000
USER nobody
ENTRYPOINT [ "java","-jar","app.jar" ]