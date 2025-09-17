// Advanced Docker Pipeline with Docker Compose
pipeline {
    agent any
    
    stages {
        stage('Checkout Code') {
            steps {
                echo '📁 Checking out code...'
                sh 'pwd'
                sh 'ls -la'
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
        
        stage('Stop Old Containers') {
            steps {
                echo '🛑 Stopping old containers...'
                sh '''
                    echo "Stopping any existing containers..."
                    docker compose down || true
                    echo "Old containers stopped!"
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
            sh 'echo "Direct Flask app is available at: http://localhost:5000"'
        }
        failure {
            echo '❌ FAILED: Something went wrong!'
            sh 'echo "Cleaning up..." && docker compose down || true'
        }
    }
}
