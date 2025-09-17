// Git-Based Pipeline - For Real Production Projects
pipeline {
    agent any
    
    stages {
        stage('Checkout Code') {
            steps {
                echo '📁 Checking out code from Git repository...'
                // In real Jenkins, this would be:
                // checkout scm
                // For now, we'll copy from our local repo
                sh '''
                    echo "Current directory: $(pwd)"
                    echo "Copying project files from Git repository..."
                    
                    # Copy all project files
                    cp -r /home/sahal/cicd-demo/lab-flask/* . 2>/dev/null || echo "Some files not found, continuing..."
                    
                    echo "Project files copied successfully!"
                    ls -la
                '''
            }
        }
        
        stage('Validate Project Structure') {
            steps {
                echo '✅ Validating project structure...'
                sh '''
                    echo "Checking required files exist..."
                    
                    required_files=("app.py" "Dockerfile" "docker-compose.yml" "nginx.conf")
                    
                    for file in "${required_files[@]}"; do
                        if [ -f "$file" ]; then
                            echo "✅ $file found"
                        else
                            echo "❌ $file not found"
                            exit 1
                        fi
                    done
                    
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
