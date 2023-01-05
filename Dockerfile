FROM adoptopenjdk/openjdk11:latest
ARG JAR_FILE=target/battleship-0.0.1-SNAPSHOT.jar
WORKDIR /opt/app
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]