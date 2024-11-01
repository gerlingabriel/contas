# Usando uma imagem base do OpenJDK
FROM openjdk:17-jdk-slim

# Define o diretório de trabalho
WORKDIR /app

# Copia o arquivo JAR gerado para dentro do contêiner
COPY target/contas-0.0.1-SNAPSHOT.jar app.jar

# Porta que a aplicação irá expor
EXPOSE 8080

# Comando para executar o aplicativo
ENTRYPOINT ["java", "-jar", "app.jar"]