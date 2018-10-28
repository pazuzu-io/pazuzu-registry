FROM openjdk:8u181-jre-alpine3.8
COPY target/pazuzu-registry.jar /pazuzu-registry.jar
EXPOSE 8080 8081
CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /pazuzu-registry.jar
