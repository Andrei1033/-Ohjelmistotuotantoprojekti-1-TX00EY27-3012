```groovy
pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        DOCKERHUB_CREDENTIALS_ID = 'projekti_ID'
        DOCKERHUB_REPO = 'khaledmarai/Classroom_Attendance_System'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {

        stage('check') {
            steps {
                bat 'git branch --show-current'
                bat 'git status'
            }
        }

        stage('build') {
            steps {
                bat 'mvn clean install'
            }
        }

        stage('test') {
            steps {
                bat 'mvn test'
            }
        }

        stage('jacoco') {
            steps {
                jacoco()
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry(
                            'https://index.docker.io/v1/',
                            DOCKERHUB_CREDENTIALS_ID
                    ) {
                        docker.image(
                                "${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}"
                        ).push()
                    }
                }
            }
        }
    }
}
```
