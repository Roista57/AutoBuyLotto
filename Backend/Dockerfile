# Dockerfile1

# 베이스 이미지 설정 (필요에 따라 Java 버전을 조정하세요)
FROM openjdk:17-jdk

# 필수 패키지 및 종속성 설치
RUN apt-get update && apt-get install -y \
    wget \
    gnupg2 \
    ca-certificates \
    fonts-liberation \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libc6 \
    libcairo2 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libexpat1 \
    libfontconfig1 \
    libgbm1 \
    libglib2.0-0 \
    libgtk-3-0 \
    libnspr4 \
    libnss3 \
    libpango-1.0-0 \
    libx11-6 \
    libxcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxrandr2 \
    libxrender1 \
    libxshmfence1 \
    libxtst6 \
    xdg-utils \
    libu2f-udev \
    libvulkan1

# Chrome 브라우저 설치
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
    && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list \
    && apt-get update \
    && apt-get install -y google-chrome-stable

# Step 2: Set the working directory inside the container
WORKDIR /app

# Step 3: Copy the jar file from host to container
COPY build/libs/Backend-0.0.1-SNAPSHOT.jar app.jar

# Step 4: Expose the port your application will run on
EXPOSE 8000

# Step 5: Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
