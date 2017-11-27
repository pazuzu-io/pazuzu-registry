FROM registry.opensource.zalan.do/stups/openjdk:latest
COPY target/pazuzu-registry.jar /pazuzu-registry.jar
EXPOSE 8080
CMD java $JAVA_OPTS $(appdynamics-agent) $(java-dynamic-memory-opts) -jar /pazuzu-registry.jar
