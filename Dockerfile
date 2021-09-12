FROM adoptopenjdk/openjdk11:jdk-11.0.8_10-alpine
VOLUME /tmp
COPY ./target/scoring-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app.jar"]