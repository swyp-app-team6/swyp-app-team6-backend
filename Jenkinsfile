pipeline {
    agent any

    environment {
        DOCKER_IMAGE = credentials('DOCKER_IMAGE')
        DOCKER_TAG   = "${env.BUILD_NUMBER}"

        TARGET_SERVER_USER = credentials('TARGET_SERVER_USER')
        TARGET_SERVER_IP   = credentials('TARGET_SERVER_IP')
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Submodule Update') {
            steps {
                echo 'Updating submodules...'
                withCredentials([usernamePassword(credentialsId: 'github-token-swyp', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASSWORD')]) {
                    sh 'git config --global url."https://${GIT_USER}:${GIT_PASSWORD}@github.com/".insteadOf "https://github.com/"'
                    sh 'git submodule init'
                    sh 'git submodule update --recursive --remote'
                }
            }
        }

        stage('Test & Build') {
            steps {
                echo 'Building Spring Boot Application...'
                sh 'chmod +x ./gradlew'
                sh './gradlew clean build'
            }
        }

        stage('Docker Build') {
            steps {
                echo "Building Docker Image: ${DOCKER_IMAGE}:${DOCKER_TAG}..."
                sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                sh "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing Image to Docker Hub...'
                withCredentials([usernamePassword(credentialsId: 'dockerhub-token-swyp', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASSWORD')]) {
                    echo $DOCKER_PASSWORD | docker login \
                    -u $DOCKER_USER \
                    --password-stdin
                    sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                    sh "docker push ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to Target EC2') {
            steps {
                echo 'Triggering deployment script on Target EC2...'
                sshagent(credentials: ['prod-ec2-ssh']) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${TARGET_SERVER_USER}@${TARGET_SERVER_IP} \
                        "bash /home/${TARGET_SERVER_USER}/deploy.sh ${DOCKER_TAG}"
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up Docker images from Jenkins Agent...'
            sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG} || true"
            sh "docker rmi ${DOCKER_IMAGE}:latest || true"
        }
        success {
            echo 'Pipeline successfully completed!'
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
        }
    }
}
