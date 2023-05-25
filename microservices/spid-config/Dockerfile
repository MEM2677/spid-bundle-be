FROM openjdk:11

LABEL org.entando.bundle-name="spid-bundle"

WORKDIR /app

COPY target/spid-config-*.jar /app/app.jar

CMD ["java", "-jar", "app.jar"]

EXPOSE 8081
