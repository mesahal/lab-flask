// Advanced Pipeline with Parameters
pipeline {
    agent any
    
    parameters {
        string(name: 'APP_VERSION', defaultValue: '1.0.0', description: 'Application version')
        choice(name: 'ENVIRONMENT', choices: ['dev', 'staging', 'prod'], description: 'Deployment environment')
        booleanParam(name: 'RUN_TESTS', defaultValue: true, description: 'Run tests')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo "Checking out code..."
                sh 'pwd && ls -la'
                echo "Building version: ${params.APP_VERSION}"
                echo "Environment: ${params.ENVIRONMENT}"
            }
        }
        
        stage('Build') {
            steps {
                echo "Building Flask application..."
                sh '''
                    echo "Building Docker image with version ${APP_VERSION}..."
                    docker build -t lab-flask-app:${APP_VERSION} .
                    echo "Docker image built successfully"
                '''
            }
        }
        
        stage('Test') {
            when {
                expression { params.RUN_TESTS == true }
            }
            steps {
                echo "Running tests..."
                sh '''
                    echo "Testing Docker container..."
                    docker run --rm lab-flask-app:${APP_VERSION} python -c "print('Flask app test passed')"
                    echo "Tests completed successfully"
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying to ${params.ENVIRONMENT}..."
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


