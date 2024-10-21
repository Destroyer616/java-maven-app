# Use an OpenJDK image as the base
FROM docker.io/library/openjdk

# Set the working directory inside the container
WORKDIR /app

# Copy the JAR file from the host to the container
COPY ./target/java-maven-app-*.jar /app/application.jar

# Expose the application port (optional, based on your app)
EXPOSE 8080

# Run the JAR file
CMD ["java", "-jar", "/app/application.jar"]

