pipeline {
    agent any

    environment {
        CONFIG_REPO = credentials('CONFIG_REPO')

        DOCKER_IMAGE = credentials('DOCKER_IMAGE')
        DOCKER_TAG   = "${env.BUILD_NUMBER}"

        TARGET_SERVER_USER = credentials('TARGET_SERVER_USER')
        TARGET_SERVER_IP   = credentials('TARGET_SERVER_IP')

        SLACK_CHANNEL = "#6팀-PR"
    }

    triggers {
        githubPush()
    }

    stages {
        stage('Clean Workspace') {
            steps {
                deleteDir()
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Submodule Update') {
            steps {
                echo 'Updating submodules...'
                withCredentials([usernamePassword(credentialsId: 'github-token-swyp', usernameVariable: 'GIT_USER', passwordVariable: 'GIT_PASSWORD')]) {
                    sh '''
                        git submodule init

                        git submodule set-url \
                            src/main/resources/config \
                            https://${GIT_USER}:${GIT_PASSWORD}@github.com/${CONFIG_REPO}

                        git submodule sync

                        git -C src/main/resources/config remote -v || true

                        git submodule update --init --recursive
                    '''
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
                    sh """
                        echo \$DOCKER_PASSWORD | docker login \
                        -u \$DOCKER_USER \
                        --password-stdin
                    """
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
//             slackSend(
//                 channel: SLACK_CHANNEL,
//                 color: '#2C953C',
//                 message: ":white_check_mark: ${env.JOB_NAME} 배포 성공! (빌드 #${env.BUILD_NUMBER})\n${env.BUILD_URL}"
//             )
        }
        failure {
            echo 'Pipeline failed. Please check the logs.'
//             slackSend(
//                 channel: SLACK_CHANNEL,
//                 color: '#FF3232',
//                 message: ":x: ${env.JOB_NAME} 배포 실패! (빌드 #${env.BUILD_NUMBER})\n${env.BUILD_URL}"
//             )
        }
    }
}
