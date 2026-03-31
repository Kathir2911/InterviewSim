# 🚀 InterviewSim Quick Start Guide

Get up and running with InterviewSim in 5 minutes!

## Prerequisites

- Java 17+ (Java 25 recommended)
- MySQL 8.0+
- [Ollama](https://ollama.com/) for AI functionality

## Option 1: Automated Setup (Recommended)

```bash
# Clone the repository
git clone https://github.com/Kathir2911/interviewsim.git
cd interviewsim

# Run the setup script
./setup.sh
```

The script will:
- ✅ Check all prerequisites
- 📥 Download required AI models
- 🔧 Configure the environment
- 🚀 Start the application

## Option 2: Manual Setup

### 1. Install Ollama

```bash
# macOS/Linux
curl -fsSL https://ollama.com/install.sh | sh

# Windows: Download from https://ollama.com/download
```

### 2. Setup AI Model

```bash
# Start Ollama
ollama serve

# Download model (in another terminal)
ollama pull llama3.2
```

### 3. Setup Database

```sql
CREATE DATABASE interviewsim;
CREATE USER 'interviewsim_user'@'localhost' IDENTIFIED BY 'your_password';
GRANT ALL PRIVILEGES ON interviewsim.* TO 'interviewsim_user'@'localhost';
```

### 4. Configure Environment

```bash
# Copy environment template
cp .env.example .env

# Edit .env with your database credentials
DB_USERNAME=interviewsim_user
DB_PASSWORD=your_password
```

### 5. Run Application

```bash
# Using Maven
./mvnw spring-boot:run

# Or build and run JAR
./mvnw clean package
java -jar target/InterviewSim-0.0.1-SNAPSHOT.jar
```

## Option 3: Docker (Easiest)

```bash
# Start with Docker Compose
docker-compose up -d

# The application will be available at http://localhost:8080
```

## 🎯 First Interview

1. **Open your browser**: http://localhost:8080
2. **Enter a coding problem**: 
   ```
   Write a function to find two numbers in an array that sum to a target value.
   ```
3. **Start the interview** and have a conversation with the AI interviewer
4. **Get scored feedback** on your performance

## 🔧 Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `password` | Database password |
| `OLLAMA_BASE_URL` | `http://localhost:11434` | Ollama server URL |
| `OLLAMA_MODEL` | `llama3.2` | AI model to use |

### Recommended AI Models

- **llama3.2** (Default) - Best balance of speed and quality
- **codellama** - Specialized for coding interviews
- **mistral** - Lightweight, faster responses

```bash
# Switch models
ollama pull codellama
# Update OLLAMA_MODEL=codellama in .env
```

## 🧪 Testing

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 🐛 Troubleshooting

### Ollama Connection Issues
```bash
# Check if Ollama is running
curl http://localhost:11434/api/tags

# Restart Ollama
pkill ollama
ollama serve
```

### Database Connection Issues
```bash
# Check MySQL status
sudo systemctl status mysql

# Reset database
mysql -u root -p -e "DROP DATABASE IF EXISTS interviewsim; CREATE DATABASE interviewsim;"
```

### Port Already in Use
```bash
# Change port in application.properties
server.port=8081
```

## 📚 API Usage

### Start Interview
```bash
curl -X POST http://localhost:8080/api/v1/interview/start \
  -H "Content-Type: application/json" \
  -d '{"problemStatement": "Reverse a string"}'
```

### Submit Answer
```bash
curl -X POST http://localhost:8080/api/v1/interview/1/answer \
  -H "Content-Type: application/json" \
  -d '{"answer": "Use two pointers approach"}'
```

## 🎉 You're Ready!

Visit http://localhost:8080 and start your first AI-powered technical interview!

---

**Need help?** Check the full [README.md](README.md) or open an issue on GitHub.