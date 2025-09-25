FROM eclipse-temurin:21-jre

WORKDIR /home/ubuntu/kustaurant-docker

COPY server/kustaurant/build/libs/*.jar kustaurant.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=prod
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar /home/ubuntu/kustaurant-docker/kustaurant.jar"]