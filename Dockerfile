FROM gradle:jdk8-alpine

COPY . /home/dilemmasask_api
WORKDIR /home/dilemmasask_api

USER root
RUN ["chown", "-R", "gradle", "/home/dilemmasask_api"]
USER gradle

RUN ["gradle", "assemble"]

ENV JDBC_DATABASE_URL=jdbc:postgresql://localhost:5432/dilemmasask_db
ENV JDBC_DATABASE_PASSWORD=password
ENV JDBC_DATABASE_USERNAME=user

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "build/libs/dilemmasask-api-0.0.1-SNAPSHOT.jar"]
