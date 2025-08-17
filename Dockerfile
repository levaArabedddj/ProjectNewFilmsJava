# 🏗️ Стадия сборки
FROM maven:3.9-eclipse-temurin AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# 🚀 Финальный контейнер (только JRE)
FROM eclipse-temurin:20-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar OOPKursova-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "OOPKursova-0.0.1-SNAPSHOT.jar"]
