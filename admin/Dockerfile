FROM --platform=linux/amd64 maven:3.9.9-eclipse-temurin-17 AS build

WORKDIR /workspace

COPY pom.xml .
COPY library/pom.xml library/pom.xml
COPY admin/pom.xml admin/pom.xml
COPY daily-lexika/pom.xml daily-lexika/pom.xml

RUN mvn -B -ntp -DskipTests dependency:go-offline

COPY library/src library/src
COPY admin/src admin/src

RUN mvn -B -ntp -DskipTests -pl admin -am clean package

FROM --platform=linux/amd64 eclipse-temurin:17-jre-alpine

WORKDIR /app
COPY --from=build /workspace/admin/target/admin-*-exec.jar /app/app.jar

ENTRYPOINT ["java","-jar","/app/app.jar"]
