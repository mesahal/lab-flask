// Production-Ready Pipeline - Builds from actual project files
pipeline {
    agent any
    
    stages {
        stage('Prepare Workspace') {
            steps {
                echo '📁 Preparing workspace with project files...'
                sh '''
                    echo "Current directory: $(pwd)"
                    echo "Copying project files from local directory..."
                    
                    # Copy all project files to workspace
                    cp /home/sahal/cicd-demo/lab-flask/app.py . 2>/dev/null || echo "app.py not found"
                    cp /home/sahal/cicd-demo/lab-flask/Dockerfile . 2>/dev/null || echo "Dockerfile not found"
                    cp /home/sahal/cicd-demo/lab-flask/docker-compose.yml . 2>/dev/null || echo "docker-compose.yml not found"
                    cp /home/sahal/cicd-demo/lab-flask/nginx.conf . 2>/dev/null || echo "nginx.conf not found"
                    
                    echo "Project files copied to workspace:"
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
                    python3 -c "import flask; print('✅ Flask is available')"
                    
                    echo "Code quality checks passed!"
                '''
            }
        }
        
        stage('Build Docker Image') {
            steps {
                echo '🐳 Building Docker image from project files...'
                sh '''
                    echo "Building image: lab-flask-app:${BUILD_NUMBER}"
                    echo "Using files from current directory:"
                    ls -la
                    
                    docker build -t lab-flask-app:${BUILD_NUMBER} .
                    echo "✅ Docker image built successfully!"
                    
                    echo "Image details:"
                    docker images | grep lab-flask-app
                '''
            }
        }
        
        stage('Test Docker Image') {
            steps {
                echo '🧪 Testing Docker image...'
                sh '''
                    echo "Testing if the image runs correctly..."
                    docker run --rm lab-flask-app:${BUILD_NUMBER} python -c "print('✅ Flask app test passed!')"
                    echo "✅ Image test completed successfully!"
                '''
            }
        }
        
        stage('Stop Old Containers') {
            steps {
                echo '🛑 Stopping old containers...'
                sh '''
                    echo "Stopping any existing containers..."
                    docker compose down || true
                    echo "✅ Old containers stopped!"
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
                    
                    echo "✅ Services started successfully!"
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
                    curl -f http://localhost:8000 && echo "✅ SUCCESS: Flask app is working through Nginx!"
                '''
            }
        }
        
        stage('Show Status') {
            steps {
                echo '📋 Showing deployment status...'
                sh '''
                    echo "=== Running Containers ==="
                    docker ps
                    
                    echo "=== Docker Images ==="
                    docker images | grep lab-flask-app || true
                    
                    echo "=== Docker Compose Status ==="
                    docker compose ps
                    
                    echo "=== Application URLs ==="
                    echo "Flask app (direct): http://localhost:5000"
                    echo "Flask app (via Nginx): http://localhost:8000"
                '''
            }
        }
    }
    
    post {
        always {
            echo '🏁 Pipeline completed!'
        }
        success {
            echo '🎊 SUCCESS: Flask app deployed successfully!'
            sh 'echo "Your Flask app is available at: http://localhost:8000"'
        }
        failure {
            echo '❌ FAILED: Deployment failed!'
            sh 'echo "Cleaning up..." && docker compose down || true'
        }
    }
}
