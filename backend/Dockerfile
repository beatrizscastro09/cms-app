# backend/Dockerfile

# Etapa 1: build do projeto
FROM maven:3.9.4-eclipse-temurin-17 AS build
WORKDIR /app

COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Etapa 2: imagem final para rodar
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copia o .war gerado pelo Maven
COPY --from=build /app/target/*.war /app/app.war

# Jetty para rodar o .war
RUN mkdir -p /var/lib/jetty/webapps
RUN apt-get update && apt-get install -y jetty9 && rm -rf /var/lib/apt/lists/*
RUN cp /app/app.war /var/lib/jetty/webapps/ROOT.war

EXPOSE 8080
CMD ["java", "-jar", "/usr/share/jetty9/start.jar"]
