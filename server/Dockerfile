FROM openjdk:8-jdk-alpine AS build
COPY . /tmp/server
WORKDIR /tmp/server
RUN ./gradlew build --no-daemon

FROM openjdk:8-jre-alpine
EXPOSE 4567
COPY --from=build /tmp/server/build/distributions/server.tar /app/
WORKDIR /app
RUN tar -xvf server.tar
CMD server/bin/server