# 🚀 InterviewSim Deployment Guide

## Quick Demo Deployment Options

### 🌟 **Option 1: Railway (Recommended)**

1. **Push to GitHub** (if not already done):
   ```bash
   git add .
   git commit -m "Ready for deployment"
   git push origin main
   ```

2. **Deploy on Railway**:
   - Go to [railway.app](https://railway.app)
   - Sign up with GitHub
   - Click "Deploy from GitHub repo"
   - Select your InterviewSim repository
   - Railway will auto-detect Java and deploy!

3. **Your demo will be live at**: `https://your-app-name.railway.app`

### 🎯 **Option 2: Render**

1. **Push to GitHub** (if not already done)

2. **Deploy on Render**:
   - Go to [render.com](https://render.com)
   - Sign up with GitHub
   - Click "New Web Service"
   - Connect your InterviewSim repository
   - Render will use the `render.yaml` configuration

3. **Your demo will be live at**: `https://your-app-name.onrender.com`

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

## 📱 **Local Demo Setup**

If you want to run it locally for demo:

1. **Start the application**:
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=production
   ```

2. **Access at**: `http://localhost:8080`

3. **For external access** (ngrok):
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
# Railway (easiest)
git push origin main
# Then connect on railway.app

# Render
git push origin main  
# Then connect on render.com

# Heroku
heroku create interviewsim-demo
git push heroku main

# Local with public access
./mvnw spring-boot:run -Dspring-boot.run.profiles=production &
ngrok http 8080
```

Your demo will be ready in minutes! 🚀