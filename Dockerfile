# Base Alpine Linux based image with OpenJDK JRE only
#FROM openjdk:8-jre-alpine
FROM openjdk:14-jdk-alpine

# This part is needed for apache batik to render images from SVGs. Its part of oracle JDK but not of openjdk:8-jre-alpine
RUN apk add --no-cache ttf-dejavu
#FROM openjdk:11-jre-slim
#EXPOSE 8080
# copy application WAR (with libraries inside)
COPY target/training-*.jar /training.jar
COPY ./gitversion.properties /gitversion.properties

# specify default command
#CMD ["/usr/bin/java", "-jar", "/training.jar"]
#ENTRYPOINT ["java", "-jar", "/training.jar"]

