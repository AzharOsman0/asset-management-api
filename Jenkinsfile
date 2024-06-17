pipeline {
    agent any

    stages {
        stage('Checkout Code') {
            steps {
                // Checkout code from the main branch
                checkout([$class: 'GitSCM', 
                          branches: [[name: '*/main']],
                          userRemoteConfigs: [[url: 'git@github.com:AzharOsman0/asset-management-api.git']]
                ])
            }
        }
        
        stage('Set up JDK 17') {
            environment {
                JAVA_HOME = tool name: 'JDK 17', type: 'hudson.model.JDK'
                PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
            }
            steps {
                // Ensure JDK 17 is installed and configured
                sh 'java -version'
            }
        }
        
        stage('Build with Maven') {
            steps {
                // Clean and build the project using Maven
                sh 'mvn clean install'
            }
        }
        
        stage('Run Unit Tests') {
            steps {
                // Run unit tests using Maven
                sh 'mvn test'
            }
        }
    }

    post {
        always {
            // Archive test results and artifacts if necessary
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Build and tests succeeded!'
        }
        failure {
            echo 'Build or tests failed!'
        }
    }
}
