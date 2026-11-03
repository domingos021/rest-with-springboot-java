# ==========================================================
# ESTÁGIO 1 - BUILD
# Compila o projeto e gera o JAR executável usando o Maven.
# Esse estágio é descartado no final: não vai para a imagem.
# ==========================================================
FROM maven:3.9-eclipse-temurin-21 AS build

# Pasta de trabalho dentro do container
WORKDIR /app

# Copia só o pom.xml primeiro e baixa as dependências.
# Assim o Docker guarda essa etapa em cache e só baixa tudo de novo
# quando o pom.xml mudar (builds seguintes ficam bem mais rápidos).
COPY pom.xml .
RUN mvn dependency:go-offline

# Copia o código-fonte e gera o JAR.
# -DskipTests: os testes usam Testcontainers, que precisam de Docker
# e não rodam dentro do build.
COPY src ./src
RUN mvn clean package -DskipTests


# ==========================================================
# ESTÁGIO 2 - EXECUÇÃO
# Imagem final, leve: só o JRE 21 (sem Maven e sem JDK) + o JAR.
# ==========================================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copia o JAR gerado no estágio anterior, renomeando para app.jar
COPY --from=build /app/target/*.jar app.jar

# Documenta a porta usada pela aplicação (padrão do Spring Boot)
EXPOSE 8080

# Comando executado quando o container iniciar: roda a aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]