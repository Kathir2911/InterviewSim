#!/bin/bash

echo "🚀 InterviewSim Setup Script"
echo "============================"

# Check if Java 25 is installed
echo "Checking Java installation..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -ge 17 ]; then
        echo "✅ Java $JAVA_VERSION found"
    else
        echo "❌ Java 17+ required. Current version: $JAVA_VERSION"
        exit 1
    fi
else
    echo "❌ Java not found. Please install Java 17 or higher."
    exit 1
fi

# Check if Maven is installed
echo "Checking Maven installation..."
if command -v mvn &> /dev/null; then
    echo "✅ Maven found"
else
    echo "⚠️  Maven not found. Using Maven wrapper instead."
fi

# Check if MySQL is running
echo "Checking MySQL connection..."
if command -v mysql &> /dev/null; then
    if mysql -u root -p -e "SELECT 1;" &> /dev/null; then
        echo "✅ MySQL connection successful"
    else
        echo "⚠️  MySQL connection failed. Please ensure MySQL is running."
        echo "   You can also use Docker Compose: docker-compose up -d mysql"
    fi
else
    echo "⚠️  MySQL client not found. Using Docker Compose is recommended."
fi

# Check if Ollama is installed
echo "Checking Ollama installation..."
if command -v ollama &> /dev/null; then
    echo "✅ Ollama found"
    
    # Check if Ollama is running
    if curl -s http://localhost:11434/api/tags &> /dev/null; then
        echo "✅ Ollama server is running"
        
        # Check if model is available
        if ollama list | grep -q "llama3.2"; then
            echo "✅ llama3.2 model found"
        else
            echo "📥 Downloading llama3.2 model..."
            ollama pull llama3.2
        fi
    else
        echo "⚠️  Ollama server not running. Starting Ollama..."
        ollama serve &
        sleep 5
        
        echo "📥 Downloading llama3.2 model..."
        ollama pull llama3.2
    fi
else
    echo "❌ Ollama not found. Please install Ollama:"
    echo "   curl -fsSL https://ollama.com/install.sh | sh"
    exit 1
fi

# Create .env file if it doesn't exist
if [ ! -f .env ]; then
    echo "📝 Creating .env file..."
    cp .env.example .env
    echo "✅ .env file created. Please update it with your database credentials."
else
    echo "✅ .env file already exists"
fi

# Build and run the application
echo "🔨 Building the application..."
if command -v mvn &> /dev/null; then
    mvn clean package -DskipTests
else
    ./mvnw clean package -DskipTests
fi

if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo ""
    echo "🚀 Starting InterviewSim..."
    echo "   Application will be available at: http://localhost:8080"
    echo "   Press Ctrl+C to stop the application"
    echo ""
    
    if command -v mvn &> /dev/null; then
        mvn spring-boot:run
    else
        ./mvnw spring-boot:run
    fi
else
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi