FROM dockerfile/java:oracle-java8
MAINTAINER Ethan Ma <ema@costco.com>
ADD target/ole-svc-1.0.0.jar /app/ole-svc-1.0.0.jar
EXPOSE 8080 9089
CMD ["java", "-jar", "/app/ole-svc-1.0.0.jar"]