FROM openjdk:11.0.1-jre-slim
COPY target/pazuzu-registry.jar /pazuzu-registry.jar
EXPOSE 8080 8081
CMD java -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -jar /pazuzu-registry.jar
