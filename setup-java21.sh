#!/bin/bash
# Script para configurar Java 21 en el proyecto SERF Sistema

echo "Configurando entorno para Java 21 LTS..."

# Configurar Java 21
export PATH="/c/Program Files/Eclipse Adoptium/jdk-21.0.8.9-hotspot/bin:$PATH"
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-21.0.8.9-hotspot"

echo "✅ Java 21 configurado"
java -version

echo ""
echo "🚀 Para compilar el proyecto ejecuta: ./mvnw clean compile"
echo "📦 Para construir el proyecto ejecuta: ./mvnw clean package"  
echo "▶️  Para ejecutar la aplicación: ./mvnw spring-boot:run"
echo ""
echo "Documentación completa en: JAVA21_UPGRADE.md"