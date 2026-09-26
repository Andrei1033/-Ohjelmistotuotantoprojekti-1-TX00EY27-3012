# Используем легковесный JRE (только рантайм) вместо полного Maven-образа
FROM eclipse-temurin:21-jre

WORKDIR /app

# Скачиваем библиотеки, необходимые для графического интерфейса JavaFX внутри Linux-контейнера
RUN apt-get update && apt-get install -y --no-install-recommends \
    libgl1-mesa-glx \
    libgtk-3-0 \
    libasound2 \
    libxxf86vm1 \
    libxrender1 \
    libxtst6 \
    libxi6 \
    libxext6 \
    && rm -rf /var/lib/apt/lists/*

# Копируем ТОЛЬКО готовый JAR-файл, собранный Jenkins на этапе 'build'
COPY target/Classroom_Attendance_System-1.0-SNAPSHOT.jar app.jar

# Настройка дисплея для GUI JavaFX
ENV DISPLAY=:0

# Запуск приложения
ENTRYPOINT ["java", "-jar", "app.jar"]
