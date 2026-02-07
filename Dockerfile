FROM --platform=linux/amd64 maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

ARG APP_NAME
RUN test -n "$APP_NAME"

COPY pom.xml .
COPY library/pom.xml library/pom.xml
COPY admin/pom.xml admin/pom.xml
COPY daily-lexika/pom.xml daily-lexika/pom.xml

RUN mvn -B -ntp -DskipTests dependency:go-offline

COPY library/src library/src
COPY admin/src admin/src
COPY daily-lexika/src daily-lexika/src

RUN mvn -B -ntp -DskipTests -pl "${APP_NAME}" -am clean package

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app

ARG APP_NAME
RUN test -n "$APP_NAME"

COPY --from=build /workspace/${APP_NAME}/target/${APP_NAME}-*-exec.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
