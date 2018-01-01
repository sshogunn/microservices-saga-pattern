FROM java:8-jre
MAINTAINER Anuar Nurmakanov <sshogunn@gmail.com>

ADD ./target/equipment-service.jar /app/
CMD ["java", "-Xmx200m", "-jar", "/app/equipment-service.jar"]

EXPOSE 8000