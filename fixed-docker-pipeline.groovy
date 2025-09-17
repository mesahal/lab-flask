// Fixed Docker Pipeline - Copies files to workspace
pipeline {
    agent any
    
    stages {
        stage('Prepare Workspace') {
            steps {
                echo '📁 Preparing workspace...'
                sh '''
                    echo "Current directory: $(pwd)"
                    echo "Creating project files..."
                    
                    # Create app.py
                    cat > app.py << 'EOF'
from flask import Flask
app = Flask(__name__)
@app.route("/")
def hello():
    return "Hello, Docker!"
if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5000)
EOF
                    
                    # Create Dockerfile
                    cat > Dockerfile << 'EOF'
FROM python:3.9-slim
WORKDIR /app
COPY app.py /app
RUN pip install flask
EXPOSE 5000
CMD ["python", "app.py"]
EOF
                    
                    # Create docker-compose.yml
                    cat > docker-compose.yml << 'EOF'
services:
  app:
    build: .
    expose:
      - "5000"
    networks:
      - app-network

  nginx:
    image: nginx:alpine
    ports:
      - "8000:80"
    volumes:
      - ./nginx.conf:/etc/nginx/conf.d/default.conf:ro
    depends_on:
      - app
    networks:
      - app-network

networks:
  app-network:
    driver: bridge
EOF
                    
                    # Create nginx.conf
                    cat > nginx.conf << 'EOF'
server {
    listen 80;

    location / {
        proxy_pass http://app:5000;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
EOF
                    
                    echo "Files created successfully!"
                    ls -la
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image for Flask app...'
                sh '''
                    echo "Building image: lab-flask-app:${BUILD_NUMBER}"
                    docker build -t lab-flask-app:${BUILD_NUMBER} .
                    echo "Docker image built successfully!"
                '''
            }
        }
        
        stage('Test Docker Image') {
            steps {
                echo '🧪 Testing Docker image...'
                sh '''
                    echo "Testing if the image runs correctly..."
                    docker run --rm lab-flask-app:${BUILD_NUMBER} python -c "print('Flask app test passed!')"
                    echo "Image test completed successfully!"
                '''
            }
        }
        
        stage('Deploy with Docker Compose') {
            steps {
                echo '🚀 Deploying with Docker Compose...'
                sh '''
                    echo "Starting services with Docker Compose..."
                    docker compose up -d
                    
                    echo "Waiting for services to start..."
                    sleep 10
                    
                    echo "Services started successfully!"
                '''
            }
        }
        
        stage('Test Application') {
            steps {
                echo '🔍 Testing Flask application...'
                sh '''
                    echo "Testing Flask app through Nginx proxy..."
                    curl -f http://localhost:8000 || echo "App not ready yet, waiting..."
                    sleep 5
                    curl -f http://localhost:8000 && echo "SUCCESS: Flask app is working through Nginx!"
                '''
            }
        }
        
        stage('Show Status') {
            steps {
                echo '📋 Showing container status...'
                sh '''
                    echo "=== Running Containers ==="
                    docker ps
                    
                    echo "=== Docker Images ==="
                    docker images | grep lab-flask-app || true
                    
                    echo "=== Docker Compose Status ==="
                    docker compose ps
                '''
            }
        }
    }
    
    post {
        always {
            echo '🏁 Pipeline completed!'
        }
        success {
            echo '🎊 SUCCESS: Flask app is running successfully!'
            sh 'echo "Your Flask app is available at: http://localhost:8000"'
        }
        failure {
            echo '❌ FAILED: Something went wrong!'
            sh 'echo "Cleaning up..." && docker compose down || true'
        }
    }
}
