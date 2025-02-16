# Build stage
FROM gradle:8.11.0-jdk21-alpine as build
WORKDIR /app
COPY . /app

# Default gradle user is `gradle`. We need to add permission on working directory for gradle to build.
USER root
RUN chown -R gradle /app
USER gradle
RUN gradle clean build

# Run stage
FROM openjdk:21-jdk
WORKDIR /home/application/java
RUN mkdir -p /shared-data

ENV JAVA_OPTS="-Duser.timezone=Asia/Ho_Chi_Minh"

# Fix the path to copy from Gradle's build directory instead of Maven's target directory
COPY --from=build /app/build/libs/*.jar waze_hcm_scraper-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar waze_hcm_scraper-0.0.1-SNAPSHOT.jar"]