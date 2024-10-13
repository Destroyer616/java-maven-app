# Use an OpenJDK image as the base
FROM openjdk:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the host to the container
COPY ./target/java-maven-app-1.1.0-SNAPSHOT.jar /app/application.jar

# Expose the application port (optional, based on your app)
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/application.jar"]

