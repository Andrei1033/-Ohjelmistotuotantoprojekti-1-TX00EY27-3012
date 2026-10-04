# ============================================================
#  Этап 1: сборка fat JAR через Maven
# ============================================================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app

# Сначала pom.xml — кэшируем зависимости
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Затем исходники
COPY src ./src
RUN mvn -B clean package -DskipTests

# ============================================================
#  Этап 2: запуск
# ============================================================
FROM eclipse-temurin:21-jre
WORKDIR /app

# JavaFX в headless-контейнере требует виртуальный дисплей и GTK
RUN apt-get update && apt-get install -y --no-install-recommends \
        xvfb \
        libgl1 \
        libgtk-3-0 \
        libxtst6 \
        libxi6 \
        libxrender1 \
        libxext6 \
        fonts-dejavu-core \
    && rm -rf /var/lib/apt/lists/*

# Копируем fat JAR и переименовываем в app.jar
COPY --from=build /app/target/Classroom_Attendance_System.jar app.jar

# Запуск через виртуальный дисплей (Xvfb)
CMD ["java", "-Dprism.order=sw", "-jar", "app.jar"]