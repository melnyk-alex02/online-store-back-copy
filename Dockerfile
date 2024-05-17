FROM happytails-reg.lav.net.ua/maven:3.8.3 AS build
COPY . /app
WORKDIR /app
RUN mvn clean install -DskipTests

FROM happytails-reg.lav.net.ua/openjdk:17
RUN useradd -M -s /sbin/nologin app
USER app
COPY --from=build /app/target/online-store-back-0.0.1.jar /app.jar

EXPOSE 4999

ENTRYPOINT ["java", "-jar", "/app.jar"]
