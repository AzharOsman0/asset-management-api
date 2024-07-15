pipeline {
    agent any

    tools {
        maven 'Maven 3.9.7'
    }

    environment {
        DOCKER_HUB_REPO = "azharosman2002/asset-api"
        DOCKER_CREDENTIALS_ID = "dockerhub-credentials"
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

        stage('Build Docker Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', env.DOCKER_CREDENTIALS_ID) {
                        def app = docker.build("${env.DOCKER_HUB_REPO}:${env.BUILD_ID}")
                        app.push()
                    }
                }
            }
        }
        
        stage('Deploy to Dev') {
            steps {
                script {
                    deploy('dev', env.DOCKER_HUB_REPO, env.BUILD_ID, 8081)
                }
            }
        }

        stage('Deploy to Test') {
            steps {
                script {
                    deploy('test', env.DOCKER_HUB_REPO, env.BUILD_ID, 8082)
                }
            }
        }

        stage('Deploy to Prod') {
            steps {
                script {
                    deploy('prod', env.DOCKER_HUB_REPO, env.BUILD_ID, 8083)
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
}

def deploy(env, repo, buildId, port) {
    if (isUnix()) {
        sh "docker stop asset-management-api-${env} || true && docker rm asset-management-api-${env} || true"
        sh "docker run -d --name asset-management-api-${env} -p ${port}:${port} ${repo}:${buildId}"
    } else {
        bat "docker stop asset-management-api-${env} || exit 0 && docker rm asset-management-api-${env} || exit 0"
        bat "docker run -d --name asset-management-api-${env} -p ${port}:${port} ${repo}:${buildId}"
    }
}
