# Base Alpine Linux based image with OpenJDK JRE only
FROM openjdk:8-jre-alpine
#EXPOSE 8080
# copy application WAR (with libraries inside)
COPY target/training-*.jar /training.jar

# specify default command
CMD ["/usr/bin/java", "-jar", "/training.jar"]

#WORKDIR /usr/src/myapp
#RUN javac Main.java
#CMD ["java", "Main"]
