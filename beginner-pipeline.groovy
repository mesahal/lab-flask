// Beginner Pipeline - Step by Step Learning
pipeline {
    agent any
    
    stages {
        stage('Welcome') {
            steps {
                echo '🎉 Welcome to Jenkins Pipelines!'
                echo 'This is your first pipeline!'
            }
        }
        
        stage('Check Environment') {
            steps {
                echo '🔍 Checking our environment...'
                sh 'echo "Current user: $(whoami)"'
                sh 'echo "Current directory: $(pwd)"'
                sh 'echo "Current time: $(date)"'
            }
        }
        
        stage('List Files') {
            steps {
                echo '📁 Listing files in our project...'
                sh 'ls -la'
            }
        }
        
        stage('Success!') {
            steps {
                echo '✅ Congratulations! Your pipeline worked!'
                echo '🚀 You are now a Jenkins pipeline expert!'
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
