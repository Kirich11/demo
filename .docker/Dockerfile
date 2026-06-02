FROM gradle:9.5.1-jdk26-alpine AS build

COPY --chown=gradle:gradle . /home/gradle/src

WORKDIR /home/gradle/src

RUN gradle build -x test --no-daemon

FROM build AS dev

CMD ["tail", "-f", "/dev/null"]

FROM eclipse-temurin:26-jdk-alpine AS app

EXPOSE 8080

COPY --from=build /home/gradle/src/build/libs/demo-0.0.1.jar /app/demo.jar

CMD ["java", "-jar", "/app/demo.jar"]
