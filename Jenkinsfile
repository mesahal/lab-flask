// Simplified Jenkinsfile without Harbor Registry
pipeline {
    agent any
    
    environment {
        DOCKER_IMAGE = 'flask-app'
        DOCKER_TAG = "${BUILD_NUMBER}"
    }
    
    stages {
        stage('Checkout Code') {
            steps {
                echo '📁 Code is automatically checked out by Jenkins'
                sh '''
                    echo "Current directory: $(pwd)"
                    echo "Project files:"
                    ls -la
                '''
            }
        }
        
        stage('Validate Project Structure') {
            steps {
                echo '✅ Validating project structure...'
                sh '''
                    echo "Checking required files exist..."
                    
                    if [ -f "app.py" ]; then
                        echo "✅ app.py found"
                    else
                        echo "❌ app.py not found"
                        exit 1
                    fi
                    
                    if [ -f "Dockerfile" ]; then
                        echo "✅ Dockerfile found"
                    else
                        echo "❌ Dockerfile not found"
                        exit 1
                    fi
                    
                    if [ -f "docker-compose.yml" ]; then
                        echo "✅ docker-compose.yml found"
                    else
                        echo "❌ docker-compose.yml not found"
                        exit 1
                    fi
                    
                    if [ -f "nginx.conf" ]; then
                        echo "✅ nginx.conf found"
                    else
                        echo "❌ nginx.conf not found"
                        exit 1
                    fi
                    
                    echo "All required files present!"
                '''
            }
        }
        
        stage('Code Quality Check') {
            steps {
                echo '🔍 Running code quality checks...'
                sh '''
                    echo "Checking Python syntax..."
                    python3 -m py_compile app.py
                    echo "✅ Python syntax is valid"
                    
                    echo "Checking Flask import..."
                    if python3 -c "import flask; print('✅ Flask is available')" 2>/dev/null; then
                        echo "✅ Flask is available on Jenkins system"
                    else
                        echo "⚠️ Flask not available on Jenkins system (will be installed in Docker)"
                        echo "This is normal - Flask will be installed when building the Docker image"
                    fi
                    
                    echo "Code quality checks passed!"
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image...'
                sh '''
                    echo "Building image: ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} .
                    docker build -t ${DOCKER_IMAGE}:latest .
                    echo "✅ Docker image built successfully"
                '''
            }
        }
        
        stage('Test Docker Image') {
            steps {
                echo '🧪 Testing Docker image...'
                sh '''
                    echo "Testing image locally..."
                    docker run -d --name test-container -p 5001:5000 ${DOCKER_IMAGE}:${DOCKER_TAG}
                    sleep 5
                    
                    echo "Testing HTTP response..."
                    if curl -f http://localhost:5001 > /dev/null 2>&1; then
                        echo "✅ Application is responding correctly"
                    else
                        echo "❌ Application is not responding"
                        exit 1
                    fi
                    
                    echo "Stopping test container..."
                    docker stop test-container
                    docker rm test-container
                    echo "✅ Docker image test passed"
                '''
            }
        }
        
        stage('Deploy with Docker Compose') {
            steps {
                echo '🚀 Deploying application...'
                sh '''
                    echo "Starting application with Docker Compose..."
                    docker compose up -d
                    echo "✅ Application deployed successfully"
                '''
            }
        }
        
        stage('Test Application') {
            steps {
                echo '🧪 Testing deployed application...'
                sh '''
                    echo "Waiting for application to start..."
                    sleep 10
                    
                    echo "Testing application endpoints..."
                    if curl -f http://localhost:8000 > /dev/null 2>&1; then
                        echo "✅ Application is accessible at http://localhost:8000"
                    else
                        echo "❌ Application is not accessible"
                        exit 1
                    fi
                    
                    echo "✅ Application test passed"
                '''
            }
        }
        
        stage('Show Status') {
            steps {
                echo '📊 Showing deployment status...'
                sh '''
                    echo "=== Docker Containers ==="
                    docker ps
                    
                    echo ""
                    echo "=== Docker Images ==="
                    docker images | grep flask-app
                    
                    echo ""
                    echo "=== Application URLs ==="
                    echo "🌐 Application: http://localhost:8000"
                    echo "🔧 Jenkins: http://localhost:8080"
                    echo "📝 GitLab: http://localhost:8082"
                '''
            }
        }
    }
    
    post {
        always {
            echo '🧹 Cleaning up...'
            sh '''
                echo "Cleaning up temporary files..."
            '''
        }
        success {
            echo '✅ Pipeline completed successfully!'
            echo '🎉 Your application is now deployed!'
        }
        failure {
            echo '❌ Pipeline failed!'
            sh '''
                echo "Cleaning up failed deployment..."
                docker compose down || true
            '''
        }
    }
}
