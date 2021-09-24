# This Dockerfile is taken from https://github.com/zorteran/wiadro-danych-kafka-streams/tree/kafka_streams_202
# I highly recommend to read following article to get more context https://medium.com/swlh/dockerizing-a-kafka-streams-app-6a5ea71fe1ef
FROM maven:3-openjdk-8-slim AS BUILD
COPY pom.xml /tmp/
COPY src /tmp/src/
WORKDIR /tmp/
RUN mvn package

FROM openjdk:8-alpine AS RUNTIME
RUN apk update && apk add --no-cache libc6-compat
RUN ln -s /lib64/ld-linux-x86-64.so.2 /lib/ld-linux-x86-64.so.2
WORKDIR /app/
COPY --from=BUILD /tmp/target/wikiapp-*-jar-with-dependencies.jar .
ENTRYPOINT ["java", "-cp", "*", "io.sytac.devjam2021.wikiapp.RawToStats"]
