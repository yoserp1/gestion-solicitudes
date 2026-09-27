FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace/backend/ms-solicitudes

COPY backend/ms-solicitudes/.mvn .mvn
COPY backend/ms-solicitudes/mvnw backend/ms-solicitudes/pom.xml ./
COPY contracts/openapi /workspace/contracts/openapi
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY backend/ms-solicitudes/src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache curl \
    && addgroup -S app \
    && adduser -S -G app -h /app app

WORKDIR /app
COPY --from=builder /workspace/backend/ms-solicitudes/target/*.jar app.jar

USER app
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
