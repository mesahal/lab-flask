// Simple Pipeline Script for Jenkins
pipeline {
    agent any
    
    stages {
        stage('Hello World') {
            steps {
                echo 'Hello World!'
            }
        }
        
        stage('Build') {
            steps {
                echo "Building..."
            }
        }
        
        stage('Test') {
            steps {
                echo "Running tests..."
            }
        }
        
        stage('Deploy') {
            steps {
                echo "Deploying..."
            }
        }
    }
}


