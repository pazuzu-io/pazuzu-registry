FROM registry.opensource.zalan.do/stups/openjdk:8u66-b17-1-12
RUN mkdir /usr/pazuzu
COPY ./target/pazuzu-web-1.0-SNAPSHOT.jar /usr/pazuzu
COPY scm-source.json /
WORKDIR /usr/pazuzu
EXPOSE 8080
VOLUME /usr/local/.pazuzu
CMD ["java", "-jar", "pazuzu-web-1.0-SNAPSHOT.jar"]
