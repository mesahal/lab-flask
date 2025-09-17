// My First Pipeline - Super Simple!
pipeline {
    agent any
    
    stages {
        stage('Step 1: Say Hello') {
            steps {
                echo 'Hello! This is my first pipeline!'
            }
        }
        
        stage('Step 2: Show Current Directory') {
            steps {
                sh 'pwd'
                sh 'ls -la'
            }
        }
        
        stage('Step 3: Show System Info') {
            steps {
                sh 'whoami'
                sh 'date'
            }
        }
    }
    
    post {
        always {
            echo 'Pipeline finished!'
        }
    }
}
