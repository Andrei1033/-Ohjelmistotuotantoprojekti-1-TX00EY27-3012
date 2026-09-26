# Используем стабильный JRE
FROM eclipse-temurin:21-jre

WORKDIR /app

# Установка графических библиотек для JavaFX (с учетом новых имен пакетов в Ubuntu)
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1 \
    libgtk-3-0 \
    libasound2t64 \
    libxxf86vm1 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# Ищем любой JAR-файл в папке target (работает и в Jenkins, и в IDE)
COPY **/Classroom_Attendance_System-1.0-SNAPSHOT.jar app.jar

ENV DISPLAY=:0

ENTRYPOINT ["java", "-jar", "app.jar"]
