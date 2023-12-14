FROM amazoncorretto:11
LABEL authors="Clemens Kurz"

WORKDIR /app

RUN mkdir "kieker-monitoring-logs"
COPY libs/kieker-1.15.2-aspectj.jar agent.jar
COPY ./build/libs/SimpleExample-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java", "-javaagent:agent.jar", "-Dorg.aspectj.weaver.showWeaveInfo=true", "-Daj.weaving.verbose=true", "-jar","app.jar"]