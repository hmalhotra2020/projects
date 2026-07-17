FROM gradle:8.5-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle :java-app:shadowJar --no-daemon -q

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/java-app/build/libs/*-all.jar app.jar
EXPOSE 4040
ENTRYPOINT ["java", \
  "--add-opens=java.base/java.lang=ALL-UNNAMED", \
  "--add-opens=java.base/java.util=ALL-UNNAMED", \
  "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED", \
  "--add-opens=java.base/sun.util.calendar=ALL-UNNAMED", \
  "--add-opens=java.base/java.nio=ALL-UNNAMED", \
  "-jar", "app.jar"]
