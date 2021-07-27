# command to build it:
# docker build --tag=currency-rate-service:latest .
# command to run it:
# docker run -p 8080:8080 currency-rate-service:latest
FROM openjdk:11-jre-slim
MAINTAINER andymur.com
COPY target/currency-rate-service.jar currency-rate-service.jar
ENTRYPOINT ["java","-jar","/currency-rate-service.jar", "server", "configuration.yml"]