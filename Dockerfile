FROM java:8
RUN mkdir /usr/pazuzu
COPY ./target/pazuzu-web-1.0-SNAPSHOT.jar /usr/pazuzu
WORKDIR /usr/pazuzu
EXPOSE 8080
VOLUME /usr/local/.pazuzu
CMD ["java", "-jar", "pazuzu-web-1.0-SNAPSHOT.jar"]
