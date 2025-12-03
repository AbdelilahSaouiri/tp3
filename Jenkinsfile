pipeline {
    agent any

    tools {
        maven 'maven'
        jdk 'jdk-21'
        dockerTool 'docker'
    }
    environment {
        NEXUS_IP = 'nexus:8081'
        NEXUS_REPO = 'maven-releases'
        GROUP_ID = 'net.ensah'
        ARTIFACT_ID = 'tp3'
        VERSION = '1.0.0'

        DOCKER_IMAGE_NAME = "mon-app-backend"
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build / Compile') {
            steps {
                echo 'Compiling code...'
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                echo 'Running tests...'
                sh 'mvn test'
            }
            post {
                always {

                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                echo 'Packaging application...'

                sh 'mvn package -DskipTests'
            }
        }


        stage('Upload to Nexus') {
            steps {
                echo 'Uploading artifact to Nexus...'
                nexusArtifactUploader(
                    nexusVersion: 'nexus3',
                    protocol: 'http',
                    nexusUrl: "${NEXUS_IP}",
                    groupId: "${GROUP_ID}",
                    version: "${VERSION}",
                    repository: "${NEXUS_REPO}",
                    credentialsId: 'nexus-credentials',
                    artifacts: [
                        [artifactId: "${ARTIFACT_ID}",
                         classifier: '',
                         file: "target/${ARTIFACT_ID}-${VERSION}.jar",
                         type: 'jar']
                    ]
                )
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    echo 'Building Docker Image...'
                    if (fileExists('Dockerfile')) {

                        sh "docker build -t ${DOCKER_IMAGE_NAME}:${VERSION} ."
                    } else {
                        error "Aucun Dockerfile trouvé à la racine !"
                    }
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo 'Pipeline terminé avec succès !'
        }
        failure {
            echo 'Le pipeline a échoué.'
        }
    }
}