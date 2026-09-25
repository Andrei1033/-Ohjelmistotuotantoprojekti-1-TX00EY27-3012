pipeline {
    agent any
    tools {
        maven 'Maven3'
    }
    environment {
        PATH = "C:\\Program Files\\Docker\\Docker\\resources\\bin;${env.PATH}"
        DOCKERHUB_CREDENTIALS_ID = 'projekti_ID'
        DOCKERHUB_REPO = 'khaledmarai/classroom_attendance_system'
        DOCKER_IMAGE_TAG = 'latest'
    }

    stages {
        stage ('check'){
            steps{
                git branch: 'main',
                        url: 'https://github.com/Andrei1033/-Ohjelmistotuotantoprojekti-1-TX00EY27-3012.git'
            }
        }
        stage('build') {
            steps {
                // Переходим в папку, где лежит pom.xml
                dir('Classroom_Attendance_System') {
                    bat 'java -version'
                    bat 'javac -version'
                    bat 'mvn -version'
                    bat 'mvn clean install'
                }
            }
        }

        stage('test') {
            steps {
                dir('Classroom_Attendance_System') {
                    bat 'mvn test'
                }
            }
        }

        stage('jacoco') {
            steps {
                dir('Classroom_Attendance_System') {
                    bat 'mvn jacoco:report'
                }
            }
        }


        stage('Build Docker Image') {
            steps {
                dir('Classroom_Attendance_System') {
                    script {
                        docker.build("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}")
                    }
                }
            }
        }

        stage('Push Docker Image to Docker Hub') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                        docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                    }
                }
            }

        }
    }
}