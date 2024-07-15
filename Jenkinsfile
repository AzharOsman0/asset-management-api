pipeline {
    agent any


    tools {
        maven 'Maven 3.9.7'
    }


    stages {
        stage('Checkout Code') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: '*/main']],
                          userRemoteConfigs: [[url: 'https://github.com/AzharOsman0/asset-management-api.git', credentialsId: '6f61d0a7-5ceb-4a19-a9bd-abbc821f603e']]
                ])
            }
        }
       
        stage('Set up JDK 17') {
            environment {
                JAVA_HOME = tool name: 'JDK 17', type: 'hudson.model.JDK'
                PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
            }
            steps {
                bat 'java -version'
            }
        }
       
        stage('Build with Maven') {
            steps {
                bat 'mvn clean install'
            }
        }
       
        stage('Run Unit Tests') {
            steps {
                bat 'mvn test'
            }
        }


        stage('Build docker Image'){
            steps{
                script{
                    def app = docker.build("my-app:${env.BUILD_ID}")
                }
            }
        }
        stage('Deploy to Dev'){
            steps{
                script{
                    deploy('dev')
                }
            }
        }
    
        stage('Deploy to Test'){
            steps{
                script{
                    deploy('test')
                }
            }
        }
 
        stage('Deploy to Prod'){
            steps{
                script{
                    deploy('prod')
                }
            }
        }

         stage('Publish to Artifactory') {
            steps {
                script {
                    def server = Artifactory.server 'jfrog-artifactory'
                    def uploadSpec = """{
                        "files": [{
                            "pattern": "target/*.jar",
                            "target": "assetapi-libs-release-local"
                        }]
                    }"""
                    server.upload(uploadSpec)
                }
            }
        }
    }


    post {
        always {
            junit 'target/surefire-reports/*.xml'
        }
        success {
            echo 'Build and tests succeeded!'
        }
        failure {
            echo 'Build or tests failed!'
        }
    }
    def deploy(env) {
    sh "docker stop asset-management-api-${env} || true && docker rm asset-management-api-${env} || true"
    sh "docker run -d --name asset-management-api-${env} -p 8080:8080 asset-management-api:${env.BUILD_ID}"
    }
}



