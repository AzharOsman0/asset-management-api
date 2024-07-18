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
                script {
                    try {
                        bat 'mvn test'
                    } catch (Exception e) {
                        echo 'Tests failed! Attempting to fix...'
                        if (isUnix()) {
                            sh 'sh fixConcurrencyBug.sh'
                        } else {
                            bat 'fixConcurrencyBug.bat'
                        }
                        bat 'mvn test'
                    }
                }
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
                    deploy('dev', env.DOCKER_HUB_REPO, env.BUILD_ID, 8081, 8080)
                }
            }
        }

        stage('Run Tests in Dev Environment') {
            steps {
                script {
                    waitForServer('dev', 8081)
                    runSmokeTests('dev', 8081)
                }
            }
        }

        stage('Deploy to Test') {
            steps {
                script {
                    deploy('test', env.DOCKER_HUB_REPO, env.BUILD_ID, 8082, 8080)
                }
            }
        }

        stage('Run Tests in Test Environment') {
            steps {
                script {
                    waitForServer('test', 8082)
                    runSmokeTests('test', 8082)
                    runFunctionalTests('test', 8082)
                    runRegressionTests('test', 8082)
                }
            }
        }

        stage('Deploy to Prod') {
            steps {
                script {
                    deploy('prod', env.DOCKER_HUB_REPO, env.BUILD_ID, 8083, 8080)
                }
            }
        }

        stage('Run Tests in Prod Environment') {
            steps {
                script {
                    waitForServer('prod', 8083)
                    runSmokeTests('prod', 8083)
                    runSanityTests('prod', 8083)
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

def deploy(env, repo, buildId, hostPort, containerPort) {
    if (isUnix()) {
        sh "docker rm -f asset-management-api-${env} || true"
        sh "docker run -d --name asset-management-api-${env} -p ${hostPort}:${containerPort} ${repo}:${buildId}"
    } else {
        bat "docker rm -f asset-management-api-${env} || exit 0"
        bat "docker run -d --name asset-management-api-${env} -p ${hostPort}:${containerPort} ${repo}:${buildId}"
    }
}

def waitForServer(env, port) {
    def endpoint = "/assets/health"
    def maxRetries = 10
    def sleepTime = 5 // seconds

    for (int i = 0; i < maxRetries; i++) {
        try {
            if (isUnix()) {
                sh "curl -f http://localhost:${port}${endpoint} || exit 1"
            } else {
                bat "curl -f http://localhost:${port}${endpoint} || exit 1"
            }
            echo "Server is up for ${env} environment."
            return
        } catch (Exception e) {
            echo "Server is not up yet for ${env} environment. Retrying in ${sleepTime} seconds..."
            sleep sleepTime
        }
    }
    error "Server did not start within ${maxRetries * sleepTime} seconds for ${env} environment."
}

def runSmokeTests(env, port) {
    def endpoint = "/assets/health"
    try {
        if (isUnix()) {
            sh "curl -f http://localhost:${port}${endpoint} || exit 1"
        } else {
            bat "curl -f http://localhost:${port}${endpoint} || exit 1"
        }
        echo "Smoke tests passed for ${env} environment."
    } catch (Exception e) {
        echo "Smoke tests failed for ${env} environment."
        throw e
    }
}

def runFunctionalTests(env, port) {
    try {
        // Test GET /assets
        if (isUnix()) {
            sh "curl -f http://localhost:${port}/assets || exit 1"
        } else {
            bat "curl -f http://localhost:${port}/assets || exit 1"
        }

        // Test POST /assets
        def jsonData = '''{"name": "Functional Test Laptop", "deviceType": "Laptop", "status": "Active", "location": "Functional Test Location", "assignedTo": "Functional Test User", "purchaseDate": "07-15-2024", "warrantyExpiry": "07-15-2025"}'''

        if (isUnix()) {
            sh "curl -X POST -H 'Content-Type: application/json' -d '${jsonData}' http://localhost:${port}/assets || exit 1"
        } else {
            bat 'curl -X POST -H "Content-Type: application/json" -d "{\\"name\\": \\"Functional Test Laptop\\", \\"deviceType\\": \\"Laptop\\", \\"status\\": \\"Active\\", \\"location\\": \\"Functional Test Location\\", \\"assignedTo\\": \\"Functional Test User\\", \\"purchaseDate\\": \\"07-15-2024\\", \\"warrantyExpiry\\": \\"07-15-2025\\"}" http://localhost:${port}/assets || exit 1'
        }

        // Test GET /assets/{id}
        def assetId = 1
        if (isUnix()) {
            sh "curl -f http://localhost:${port}/assets/${assetId} || exit 1"
        } else {
            bat "curl -f http://localhost:${port}/assets/${assetId} || exit 1"
        }

        // Test PUT /assets/{id}
        def updateData = '''{"name": "Updated Functional Test Laptop", "deviceType": "Laptop", "status": "Active", "location": "Updated Functional Test Location", "assignedTo": "Updated Functional Test User", "purchaseDate": "07-15-2024", "warrantyExpiry": "07-15-2025"}'''

        if (isUnix()) {
            sh "curl -X PUT -H 'Content-Type: application/json' -d '${updateData}' http://localhost:${port}/assets/${assetId} || exit 1"
        } else {
            bat 'curl -X PUT -H "Content-Type: application/json" -d "{\\"name\\": \\"Updated Functional Test Laptop\\", \\"deviceType\\": \\"Laptop\\", \\"status\\": \\"Active\\", \\"location\\": \\"Updated Functional Test Location\\", \\"assignedTo\\": \\"Updated Functional Test User\\", \\"purchaseDate\\": \\"07-15-2024\\", \\"warrantyExpiry\\": \\"07-15-2025\\"}" http://localhost:${port}/assets/${assetId} || exit 1'
        }

        // Test DELETE /assets/{id} (if applicable)
        if (isUnix()) {
            sh "curl -X DELETE -f http://localhost:${port}/assets/${assetId} || exit 1"
        } else {
            bat "curl -X DELETE -f http://localhost:${port}/assets/${assetId} || exit 1"
        }

        echo "Functional tests passed for ${env} environment."
    } catch (Exception e) {
        echo "Functional tests failed for ${env} environment."
        throw e
    }
}

def runRegressionTests(env, port) {
    try {
        def jsonData = '''{"name": "Test Laptop", "deviceType": "Laptop", "status": "Active", "location": "Test Location", "assignedTo": "Test User", "purchaseDate": "07-15-2024", "warrantyExpiry": "07-15-2025"}'''

        if (isUnix()) {
            sh """
                curl -X POST -H 'Content-Type: application/json' -d '${jsonData}' http://localhost:${port}/assets || exit 1
                curl -f http://localhost:${port}/assets || exit 1
            """
        } else {
            bat '''
                curl -X POST -H "Content-Type: application/json" -d "{\\"name\\": \\"Test Laptop\\", \\"deviceType\\": \\"Laptop\\", \\"status\\": \\"Active\\", \\"location\\": \\"Test Location\\", \\"assignedTo\\": \\"Test User\\", \\"purchaseDate\\": \\"07-15-2024\\", \\"warrantyExpiry\\": \\"07-15-2025\\"}" http://localhost:${port}/assets || exit 1
                curl -f http://localhost:${port}/assets || exit 1
            '''
        }
        echo "Regression tests passed for ${env} environment."
    } catch (Exception e) {
        echo "Regression tests failed for ${env} environment."
        throw e
    }
}

def runSanityTests(env, port) {
    def endpoint = "/assets/health"
    try {
        if (isUnix()) {
            sh "curl -f http://localhost:${port}${endpoint} || exit 1"
        } else {
            bat "curl -f http://localhost:${port}${endpoint} || exit 1"
        }
        echo "Sanity tests passed for ${env} environment."
    } catch (Exception e) {
        echo "Sanity tests failed for ${env} environment."
        throw e
    }
}
