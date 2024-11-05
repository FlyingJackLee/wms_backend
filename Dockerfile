FROM openjdk:21-slim
LABEL authors="Zumin Li"

WORKDIR /app
ADD target/wms_backend_beta.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
