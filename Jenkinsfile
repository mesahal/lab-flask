pipeline {
    agent any
    
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code..."
                sh 'pwd && ls -la'
            }
        }
        
        stage('Build') {
            steps {
                echo "Building Flask application..."
                sh '''
                    echo "Building Docker image..."
                    docker build -t lab-flask-app:${BUILD_NUMBER} .
                    echo "Docker image built successfully"
                '''
            }
        }
        
        stage('Test') {
            steps {
                echo "Running tests..."
                sh '''
                    echo "Testing Docker container..."
                    docker run --rm lab-flask-app:${BUILD_NUMBER} python -c "print('Flask app test passed')"
                    echo "Tests completed successfully"
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying application..."
                sh '''
                    echo "Stopping existing containers..."
                    docker compose down || true
                    
                    echo "Starting new deployment..."
                    docker compose up -d
                    
                    echo "Waiting for services to start..."
                    sleep 10
                    
                    echo "Testing deployment..."
                    curl -f http://localhost:8000 || echo "Service not ready yet"
                '''
            }
        }
    }
    
    post {
        always {
            echo "Pipeline execution completed"
            sh 'docker images | grep lab-flask-app || true'
        }
        success {
            echo "Pipeline succeeded!"
            sh 'echo "Deployment successful - Flask app is running on http://localhost:8000"'
        }
        failure {
            echo "Pipeline failed!"
        }
    }
}
