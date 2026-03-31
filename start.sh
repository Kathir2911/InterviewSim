#!/bin/bash

echo "🚀 Starting InterviewSim..."
echo "Port: ${PORT:-8080}"
echo "Profile: ${SPRING_PROFILES_ACTIVE:-production}"

# Start the application
exec java -Dserver.port=${PORT:-8080} \
          -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-production} \
          -Xmx512m \
          -jar target/InterviewSim-0.0.1-SNAPSHOT.jar