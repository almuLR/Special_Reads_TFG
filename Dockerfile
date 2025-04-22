# 1) Build con Maven preinstalado
FROM maven:3.8.7-eclipse-temurin-17 AS build
WORKDIR /app

# Sólo copiamos el pom para cachear dependencias
COPY pom.xml ./
RUN mvn dependency:go-offline -B

# Ahora copiamos el código y empaquetamos
COPY src ./src
RUN mvn package -DskipTests -B

# 2) Runtime ligero con JRE
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
