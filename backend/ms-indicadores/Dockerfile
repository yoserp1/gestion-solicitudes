FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /workspace/backend/ms-indicadores

COPY backend/ms-indicadores/.mvn .mvn
COPY backend/ms-indicadores/mvnw backend/ms-indicadores/pom.xml ./
COPY contracts/openapi /workspace/contracts/openapi
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY backend/ms-indicadores/src src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-alpine AS runtime

RUN apk add --no-cache curl \
    && addgroup -S app \
    && adduser -S -G app -h /app app

WORKDIR /app
COPY --from=builder /workspace/backend/ms-indicadores/target/*.jar app.jar

USER app
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
