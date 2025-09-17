// Simple Working Pipeline - No Docker permissions needed
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
    return "Hello, Jenkins Pipeline!"
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
                    
                    echo "Files created successfully!"
                    ls -la
                '''
            }
        }
        
        stage('Validate Files') {
            steps {
                echo '✅ Validating created files...'
                sh '''
                    echo "Checking app.py..."
                    head -5 app.py
                    
                    echo "Checking Dockerfile..."
                    head -5 Dockerfile
                    
                    echo "File validation completed!"
                '''
            }
        }
        
        stage('Test Python Code') {
            steps {
                echo '🐍 Testing Python code...'
                sh '''
                    echo "Testing Python syntax..."
                    python3 -m py_compile app.py
                    echo "Python syntax is valid!"
                    
                    echo "Testing Flask import..."
                    python3 -c "import flask; print('Flask import successful!')"
                    echo "Flask is available!"
                '''
            }
        }
        
        stage('Show Docker Commands') {
            steps {
                echo '🐳 Showing Docker commands (for reference)...'
                sh '''
                    echo "To build Docker image, run:"
                    echo "docker build -t lab-flask-app:${BUILD_NUMBER} ."
                    
                    echo "To run Docker container, run:"
                    echo "docker run -d -p 5000:5000 lab-flask-app:${BUILD_NUMBER}"
                    
                    echo "To test the app, run:"
                    echo "curl http://localhost:5000"
                '''
            }
        }
        
        stage('Success!') {
            steps {
                echo '🎊 SUCCESS: Pipeline completed successfully!'
                sh '''
                    echo "Your Flask app files are ready!"
                    echo "You can now run Docker commands manually:"
                    echo "1. docker build -t my-flask-app ."
                    echo "2. docker run -d -p 5000:5000 my-flask-app"
                    echo "3. curl http://localhost:5000"
                '''
            }
        }
    }
    
    post {
        always {
            echo '🏁 Pipeline completed!'
        }
        success {
            echo '🎊 SUCCESS: Everything worked perfectly!'
        }
        failure {
            echo '❌ FAILED: Something went wrong!'
        }
    }
}
