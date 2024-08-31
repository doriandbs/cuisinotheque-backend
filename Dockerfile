FROM maven:3.8.5-openjdk-17 as BUILD
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /target/backend-1.0-SNAPSHOT.jar back.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","back.jar"]