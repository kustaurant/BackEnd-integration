FROM eclipse-temurin:21-jdk

WORKDIR /home/ubuntu/crawler-docker

# playwright 설치
RUN apt-get update && apt-get install -y maven
RUN mvn exec:java -e \
    -Dexec.mainClass=com.microsoft.playwright.CLI \
    -Dexec.args="install-deps"

COPY server/crawler/build/libs/*.jar crawler.jar

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar /home/ubuntu/crawler-docker/crawler.jar"]