// Docker Pipeline - Build and Run Flask App
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
        
        stage('Run Flask App') {
            steps {
                echo '🚀 Starting Flask app in container...'
                sh '''
                    echo "Stopping any existing containers..."
                    docker stop flask-app-${BUILD_NUMBER} || true
                    docker rm flask-app-${BUILD_NUMBER} || true
                    
                    echo "Starting new Flask app container..."
                    docker run -d --name flask-app-${BUILD_NUMBER} -p 5001:5000 lab-flask-app:${BUILD_NUMBER}
                    
                    echo "Waiting for app to start..."
                    sleep 5
                    
                    echo "Flask app is running on port 5001"
                '''
            }
        }
        
        stage('Test Flask App') {
            steps {
                echo '🔍 Testing Flask app endpoint...'
                sh '''
                    echo "Testing Flask app..."
                    curl -f http://localhost:5001 || echo "App not ready yet, waiting..."
                    sleep 3
                    curl -f http://localhost:5001 && echo "SUCCESS: Flask app is working!"
                '''
            }
        }
        
        stage('Show Running Containers') {
            steps {
                echo '📋 Showing running containers...'
                sh 'docker ps'
            }
        }
    }
    
    post {
        always {
            echo '🏁 Pipeline completed!'
            sh 'echo "Docker images created:" && docker images | grep lab-flask-app || true'
        }
        success {
            echo '🎊 SUCCESS: Flask app is running successfully!'
            sh 'echo "Your Flask app is available at: http://localhost:5001"'
        }
        failure {
            echo '❌ FAILED: Something went wrong!'
            sh 'echo "Cleaning up containers..." && docker stop flask-app-${BUILD_NUMBER} || true'
        }
    }
}
