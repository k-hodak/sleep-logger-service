FROM eclipse-temurin:21.0.9_10-jdk

WORKDIR /app

COPY build.gradle gradlew settings.gradle ./
COPY gradle/ gradle/

COPY src/ src

RUN apt-get update && apt-get install -y findutils

RUN ./gradlew build

ENTRYPOINT ["java","-jar","build/libs/sleep-0.0.1-SNAPSHOT.jar"]
