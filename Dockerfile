FROM folioci/alpine-jre-openjdk17:latest

USER root

# Copy your fat jar to the container
ENV APP_FILE edge-dcb-fat.jar

# - should be a single jar file
ARG JAR_FILE=./target/*.jar
# - copy
COPY ${JAR_FILE} ${JAVA_APP_DIR}/${APP_FILE}

# Expose this port locally in the container.
EXPOSE 8081
