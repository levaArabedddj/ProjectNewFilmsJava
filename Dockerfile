# Используем официальный образ OpenJDK
FROM eclipse-temurin:20-jdk

# Указываем рабочую директорию
WORKDIR /app

# Копируем jar файл, собранный Gradle или Maven
COPY target/*.jar OOPKursova-0.0.1-SNAPSHOT.jar

# Открываем порт (если Render требует)
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "OOPKursova-0.0.1-SNAPSHOT.jar"]
