# Use official OpenJDK base image
FROM openjdk:21-jdk

# Add metadata (optional)
LABEL maintainer="asmitumrao@gmail.com"

# Add app jar to container
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Run the jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
