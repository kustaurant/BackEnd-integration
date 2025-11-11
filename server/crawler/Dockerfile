FROM eclipse-temurin:21-jdk

WORKDIR /home/ubuntu/crawler-docker

COPY server/crawler/build/libs/*.jar crawler.jar

RUN apt-get update && apt-get install -y --no-install-recommends \
    libglib2.0-0t64 \
    libnss3 \
    libnspr4 \
    libdbus-1-3 \
    libatk1.0-0t64 \
    libatk-bridge2.0-0t64 \
    libatspi2.0-0t64 \
    libx11-6 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libxrender1 \
    libpango-1.0-0 \
    libcairo2 \
    libdrm2 \
    libgbm1 \
    libxkbcommon0 \
    fonts-liberation \
    libasound2 \
    libxshmfence1 \
    wget \
    && rm -rf /var/lib/apt/lists/*

RUN mkdir -p /playwright && \
    wget -q https://playwright.azureedge.net/builds/chromium/1128/chromium-linux.zip -O /tmp/chromium.zip && \
    apt-get install -y unzip && unzip /tmp/chromium.zip -d /playwright && rm -f /tmp/chromium.zip

EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_TOOL_OPTIONS -jar /home/ubuntu/crawler-docker/crawler.jar"]