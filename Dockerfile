# Step 1: Use a Gradle image to build the application
FROM eclipse-temurin:21-jdk AS build

# Set the working directory
WORKDIR /usr/app/

# Copy all project files to the container
COPY . .
RUN chmod +x gradlew
# Run the Gradle build
RUN ./gradlew bootJar
# Step 2: Use a JDK image to run the application
FROM eclipse-temurin:21-jdk

# Set environment variables
ENV JAR_NAME=app.jar
ENV APP_HOME=/usr/app/

# Set the working directory in the second stage
WORKDIR $APP_HOME

# Copy the built application from the Gradle container
COPY --from=build $APP_HOME/build/libs/*.jar app.jar

# Expose port 8081
EXPOSE 8081

# Command to run the application
ENTRYPOINT ["java", "-jar", "/usr/app/app.jar"]
