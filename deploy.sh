#!/bin/bash

echo "🚀 InterviewSim Deployment Helper"
echo "================================="

# Check if git is initialized
if [ ! -d ".git" ]; then
    echo "📝 Initializing Git repository..."
    git init
    git add .
    git commit -m "Initial commit - InterviewSim ready for deployment"
fi

echo ""
echo "🌟 Choose your deployment option:"
echo "1. Railway (Recommended - Free & Easy)"
echo "2. Render (Free tier available)"  
echo "3. Heroku (Requires credit card)"
echo "4. Local demo with ngrok"
echo ""

read -p "Enter your choice (1-4): " choice

case $choice in
    1)
        echo ""
        echo "🚂 Railway Deployment:"
        echo "1. Push your code to GitHub (if not already done)"
        echo "2. Go to https://railway.app"
        echo "3. Sign up with GitHub"
        echo "4. Click 'Deploy from GitHub repo'"
        echo "5. Select your InterviewSim repository"
        echo "6. Railway will auto-detect and deploy!"
        echo ""
        echo "📋 Your demo will be live at: https://your-app-name.railway.app"
        ;;
    2)
        echo ""
        echo "🎨 Render Deployment:"
        echo "1. Push your code to GitHub (if not already done)"
        echo "2. Go to https://render.com"
        echo "3. Sign up with GitHub"
        echo "4. Click 'New Web Service'"
        echo "5. Connect your InterviewSim repository"
        echo "6. Render will use the render.yaml configuration"
        echo ""
        echo "📋 Your demo will be live at: https://your-app-name.onrender.com"
        ;;
    3)
        echo ""
        echo "🔥 Heroku Deployment:"
        echo "Installing Heroku CLI..."
        
        # Check if Heroku CLI is installed
        if ! command -v heroku &> /dev/null; then
            echo "Installing Heroku CLI..."
            curl https://cli-assets.heroku.com/install.sh | sh
        fi
        
        echo "1. Login to Heroku:"
        heroku login
        
        echo "2. Creating Heroku app..."
        read -p "Enter your app name (e.g., my-interviewsim): " appname
        heroku create $appname
        
        echo "3. Deploying..."
        git add .
        git commit -m "Deploy to Heroku"
        git push heroku main
        
        echo "📋 Your demo is live at: https://$appname.herokuapp.com"
        ;;
    4)
        echo ""
        echo "🏠 Local Demo with Public Access:"
        
        # Check if ngrok is installed
        if ! command -v ngrok &> /dev/null; then
            echo "Installing ngrok..."
            npm install -g ngrok
        fi
        
        echo "Starting application..."
        ./mvnw spring-boot:run -Dspring-boot.run.profiles=production &
        APP_PID=$!
        
        echo "Waiting for app to start..."
        sleep 10
        
        echo "Creating public tunnel..."
        ngrok http 8080 &
        NGROK_PID=$!
        
        echo ""
        echo "📋 Your demo is now accessible at the ngrok URL shown above!"
        echo "Press Ctrl+C to stop the demo"
        
        # Wait for user to stop
        trap "kill $APP_PID $NGROK_PID" EXIT
        wait
        ;;
    *)
        echo "Invalid choice. Please run the script again."
        ;;
esac

echo ""
echo "🎉 Deployment setup complete!"
echo "📖 For more details, check DEPLOYMENT.md"