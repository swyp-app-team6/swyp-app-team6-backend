pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('test') {
            steps {
                echo 'some jobs'
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline Succeeded! Ready for review.'
        }
        failure {
            echo 'CI Pipeline Failed. Please check the logs.'
        }
    }
}