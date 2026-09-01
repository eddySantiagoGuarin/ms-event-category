FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder
WORKDIR /app

<<<<<<< Updated upstream
# Crear un settings.xml con un espejo público de Maven Central (evita el bloqueo HTTP 403)
RUN mkdir -p /root/.m2 && echo '<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0" \
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" \
  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd"> \
  <mirrors> \
    <mirror> \
      <id>aliyunmaven</id> \
      <mirrorOf>central</mirrorOf> \
      <name>Aliyun Public Mirror</name> \
      <url>https://maven.aliyun.com/repository/public</url> \
    </mirror> \
  </mirrors> \
</settings>' > /root/.m2/settings.xml

# Copiar e instalar la librería común
COPY wd-lib-common ./wd-lib-common
RUN mvn -f wd-lib-common/pom.xml clean install -DskipTests

# Copiar y compilar el microservicio
COPY ms-event-category ./ms-event-category
=======
# 1. Compilar e instalar la librería común (wd-lib-common)
COPY wd-lib-common ./wd-lib-common
RUN mvn -f wd-lib-common/pom.xml clean install -DskipTests

# 2. Descargar dependencias del microservicio (Caché de capas)
COPY ms-event-category/pom.xml ./ms-event-category/
RUN mvn -f ms-event-category/pom.xml dependency:go-offline -B

# 3. Copiar código fuente y empaquetar
COPY ms-event-category/src ./ms-event-category/src
>>>>>>> Stashed changes
RUN mvn -f ms-event-category/pom.xml clean package -DskipTests

# Etapa final (Runtime)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring
USER spring

COPY --from=builder /app/ms-event-category/target/*.jar app.jar

EXPOSE 9092
ENTRYPOINT ["java", "-jar", "app.jar"]