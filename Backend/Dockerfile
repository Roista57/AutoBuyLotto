# Dockerfile1

# Step 1: Base image
FROM openjdk:17-jdk-alpine

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the jar file from host to container
COPY build/libs/Backend-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port your application will run on
EXPOSE 8000

# Step 5: Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
