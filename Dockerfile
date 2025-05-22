FROM openjdk:17-jdk-alpine
COPY target/hr-platform-0.0.1.jar app.jar
COPY src/main/resources/application-secret.properties application-secret.properties
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "/app.jar", "--spring.profiles.active=secret", "--spring.config.location=classpath:/application.properties,classpath:/application-secret.properties"]
