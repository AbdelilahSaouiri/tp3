# 1. Utiliser une image de base officielle et maintenue pour Java 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Créer un répertoire de travail (optionnel mais recommandé)
WORKDIR /app

# 3. (Important) Définir l'argument pour le fichier JAR
# Cela permet de copier le fichier généré quel que soit son nom exact
ARG JAR_FILE=target/*.jar

# 4. Copier le fichier JAR de votre PC vers l'image Docker
COPY ${JAR_FILE} app.jar

# 5. La commande pour lancer l'application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]