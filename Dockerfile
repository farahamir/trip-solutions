# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jdk

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven/Gradle built jar file into the container
COPY target/e-mobility-charging-solutions-0.0.1-SNAPSHOT.jar /app/app.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
