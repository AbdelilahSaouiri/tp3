FROM openjdk:21-jdk
WORKDIR /app
COPY target/*.jar /app/spring.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/spring.jar"]