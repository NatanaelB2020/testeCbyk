# Use uma imagem do OpenJDK
FROM openjdk:17-jdk-slim

# Diretório de trabalho dentro do container
WORKDIR /app

# Copie o arquivo .jar gerado pelo Maven para o container
COPY target/desafio-0.0.1-SNAPSHOT.jar /app/desafio.jar

# Comando para rodar a aplicação
CMD ["java", "-jar", "desafio.jar"]
