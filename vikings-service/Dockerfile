FROM java:8-jre
MAINTAINER Anuar Nurmakanov <sshogunn@gmail.com>

ADD ./target/vikings-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/vikings-service.jar"]

EXPOSE 6000