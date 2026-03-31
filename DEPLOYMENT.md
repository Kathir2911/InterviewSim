# 🚀 InterviewSim Deployment Guide

## Quick Demo Deployment Options

### 🌟 **Option 1: Render (Recommended - Docker)**

1. **Push to GitHub** (if not already done):
   ```bash
   git add .
   git commit -m "Ready for Render deployment"
   git push origin main
   ```

2. **Deploy on Render**:
   - Go to [render.com](https://render.com)
   - Sign up with GitHub
   - Click "New +" → "Web Service"
   - Select your InterviewSim repository
   - **Environment**: Select "Docker"
   - **Plan**: Select "Free"
   - Render will auto-detect the Dockerfile and render.yaml

3. **Your demo will be live at**: `https://your-app-name.onrender.com`

### 🎯 **Option 2: Railway**

1. **Push to GitHub** (if not already done)

2. **Deploy on Railway**:
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub
   - Click "Deploy from GitHub repo"
   - Select your InterviewSim repository
   - Railway will auto-detect and deploy!

3. **Your demo will be live at**: `https://your-app-name.railway.app`

### 🔥 **Option 3: Heroku**

1. **Install Heroku CLI**:
   ```bash
   # Ubuntu/Debian
   curl https://cli-assets.heroku.com/install.sh | sh
   ```

2. **Deploy**:
   ```bash
   heroku login
   heroku create your-interviewsim-app
   git push heroku main
   ```

3. **Your demo will be live at**: `https://your-interviewsim-app.herokuapp.com`

## 🐳 **Docker Configuration**

The project includes a production-ready Dockerfile:

```dockerfile
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline -B
COPY src src
RUN ./mvnw clean package -DskipTests
EXPOSE 8080
CMD ["java", "-Dserver.port=${PORT}", "-Dspring.profiles.active=production", "-Xmx512m", "-jar", "target/InterviewSim-0.0.1-SNAPSHOT.jar"]
```

## 📱 **Local Demo Setup**

If you want to run it locally for demo:

1. **Using Docker**:
   ```bash
   # Build the image
   docker build -t interviewsim .
   
   # Run the container
   docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=production interviewsim
   ```

2. **Using Maven**:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=production
   ```

3. **Access at**: `http://localhost:8080`

4. **For external access** (ngrok):
   ```bash
   # Install ngrok
   npm install -g ngrok
   
   # Expose local server
   ngrok http 8080
   ```
   
   This gives you a public URL like: `https://abc123.ngrok.io`

## 🎨 **Demo Features**

Your deployed demo will include:

✅ **Full AI Interview Simulator**
- Smart question progression (no repetition)
- Dynamic scoring system (0-10 scale)
- Personalized feedback generation
- Conversation tracking
- Session management

✅ **Fallback Mode**
- Works without Ollama (perfect for demos)
- Structured question flow
- Rule-based scoring
- Professional feedback

✅ **Clean UI**
- Responsive design
- Real-time updates
- Professional styling
- Easy to use interface

## 🔧 **Configuration**

The app is configured to:
- Use H2 in-memory database (no setup needed)
- Run in fallback mode (no Ollama required)
- Handle all edge cases gracefully
- Provide realistic interview experience
- Use Java 21 for better compatibility

## 📞 **Demo Script**

When showing the demo:

1. **Start**: "This is InterviewSim, an AI-powered technical interview simulator"
2. **Enter Problem**: "Let me enter a coding problem like 'Reverse a string'"
3. **Show Flow**: Demonstrate the natural question progression
4. **Highlight Features**: 
   - No repeated questions
   - Smart scoring (0-10)
   - Personalized feedback
   - Professional interview experience
5. **End Session**: Show the detailed feedback report

## 🎯 **Quick Deploy Commands**

```bash
# Render (Docker - Recommended)
git add .
git commit -m "Deploy to Render"
git push origin main
# Then connect on render.com with Docker environment

# Railway (easiest)
git push origin main
# Then connect on railway.app

# Heroku
heroku create interviewsim-demo
git push heroku main

# Local Docker test
docker build -t interviewsim .
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=production interviewsim

# Local with public access
./mvnw spring-boot:run -Dspring-boot.run.profiles=production &
ngrok http 8080
```

Your demo will be ready in minutes! 🚀