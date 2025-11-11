FROM mcr.microsoft.com/playwright/java:v1.55.0

WORKDIR /home/ubuntu/crawler-docker

COPY server/crawler/build/libs/*.jar crawler.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar /home/ubuntu/crawler-docker/crawler.jar"]