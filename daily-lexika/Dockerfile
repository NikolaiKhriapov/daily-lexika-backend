FROM maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY library/pom.xml library/pom.xml
COPY daily-lexika/pom.xml daily-lexika/pom.xml
COPY admin/pom.xml admin/pom.xml

RUN mvn -B -ntp -DskipTests dependency:go-offline

COPY library/src library/src
COPY daily-lexika/src daily-lexika/src

RUN mvn -B -ntp -DskipTests -pl daily-lexika -am clean package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /workspace/daily-lexika/target/daily-lexika-*-exec.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
