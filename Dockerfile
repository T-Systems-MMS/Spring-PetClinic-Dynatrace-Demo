FROM openjdk:8-jre-slim
MAINTAINER T-Systems-MMS APM

COPY /target/spring-petclinic-2.0.0.BUILD-SNAPSHOT.jar /opt/spring-petclinic.jar
CMD ["java","-jar","/opt/spring-petclinic.jar"]
