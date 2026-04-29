FROM eclipse-temurin:25-jdk-jammy
WORKDIR /app
COPY . .
RUN ./gradlew :services:service-b:bootJar --no-daemon --stacktrace || true
ENTRYPOINT ["java","-jar","build/libs/service-b.jar"]
