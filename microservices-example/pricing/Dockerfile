FROM gradle:latest AS builder
WORKDIR /home/gradle/project
COPY . /home/gradle/project
# generate Gradle wrapper inside the builder and use it to build (makes image independent of host)
RUN gradle wrapper && ./gradlew clean build -x test --no-daemon

FROM eclipse-temurin:latest
VOLUME /tmp
COPY --from=builder /home/gradle/project/build/libs/pricing-1.0.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.profiles.active=docker","-jar","/app.jar"]
