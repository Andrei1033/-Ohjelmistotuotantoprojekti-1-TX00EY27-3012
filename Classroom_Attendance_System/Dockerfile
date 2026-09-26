FROM maven:3.9.6-eclipse-temurin-21
WORKDIR /app
COPY pom.xml .
COPY . /app
RUN mvn clean package -DskipTests
CMD ["java", "-jar", "target/Classroom_Attendance_System-1.0-SNAPSHOT.jar"]


