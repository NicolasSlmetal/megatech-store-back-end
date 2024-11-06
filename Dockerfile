FROM openjdk:21-jdk

WORKDIR /app

ARG APP_NAME="store-0.0.1.jar"

COPY target/${APP_NAME} /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]