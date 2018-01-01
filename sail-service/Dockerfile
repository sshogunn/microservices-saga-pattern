FROM java:8-jre
MAINTAINER Anuar Nurmakanov <sshogunn@gmail.com>

ADD ./target/sail-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/sail-service.jar"]

EXPOSE 7000