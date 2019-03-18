FROM openjdk:11
EXPOSE 8080
VOLUME /tmp
COPY ./build/libs/mangafeed-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
