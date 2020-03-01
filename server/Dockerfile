# Build the server files in a build stage
FROM openjdk:13-jdk-alpine AS build
# Copy the project into /tmp/server
COPY . /tmp/server
WORKDIR /tmp/server
# Build the server with Gradle
RUN ./gradlew build --no-daemon

# Build the production server container
FROM openjdk:13-jdk-alpine
EXPOSE 4567
# Copy the distribution tar that was built in the build stage
COPY --from=build /tmp/server/build/distributions/server.tar /app/
WORKDIR /app
# Untar the server files
RUN tar -xvf server.tar
# Run the server when the container is started
CMD server/bin/server