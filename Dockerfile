FROM openjdk:8-jdk-alpine
EXPOSE 8090
ADD target/tweet-application.jar tweet-application.jar
ENTRYPOINT ["java","-jar","/tweet-application.jar"]

