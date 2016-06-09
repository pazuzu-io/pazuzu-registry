FROM registry.opensource.zalan.do/stups/openjdk:8-26
RUN mkdir /usr/pazuzu
COPY ./target/pazuzu-registry.jar /usr/pazuzu
COPY scm-source.json /
WORKDIR /usr/pazuzu
EXPOSE 8080
CMD ["java", "-jar", "pazuzu-registry.jar"]
