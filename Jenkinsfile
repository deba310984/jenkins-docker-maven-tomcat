// Declarative Jenkins pipeline: build -> test -> package -> image -> deploy.
//
// Prerequisites configured in Jenkins (Manage Jenkins > Tools):
//   - a JDK named "jdk21"
//   - a Maven named "maven3"
// The agent must have Docker available for the image/deploy stages.
// DOCKER_IMAGE / registry credentials are wired for an optional push.

pipeline {
    agent any

    tools {
        jdk   'jdk21'
        maven 'maven3'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    environment {
        DOCKER_IMAGE = 'debjit/java-tomcat-app'
        CONTAINER    = 'java-tomcat-app'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build & Test') {
            // Compiles and runs unit tests. A failing test fails the build.
            steps {
                sh 'mvn -B clean verify'
            }
            post {
                always {
                    junit testResults: 'target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        stage('Package WAR') {
            steps {
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t $DOCKER_IMAGE:$BUILD_NUMBER -t $DOCKER_IMAGE:latest .'
            }
        }

        stage('Docker Push') {
            // Pushes only when Docker Hub credentials (id: dockerhub) are configured.
            when { expression { return env.PUSH_IMAGE == 'true' } }
            steps {
                withCredentials([usernamePassword(credentialsId: 'dockerhub',
                        usernameVariable: 'DH_USER', passwordVariable: 'DH_PASS')]) {
                    sh 'echo "$DH_PASS" | docker login -u "$DH_USER" --password-stdin'
                    sh 'docker push $DOCKER_IMAGE:$BUILD_NUMBER'
                    sh 'docker push $DOCKER_IMAGE:latest'
                }
            }
        }

        stage('Deploy') {
            // Simple single-host deploy: replace the running container.
            steps {
                sh '''
                    docker rm -f $CONTAINER || true
                    docker run -d --name $CONTAINER -p 8080:8080 $DOCKER_IMAGE:$BUILD_NUMBER
                '''
            }
        }

        stage('Smoke Test') {
            steps {
                sh '''
                    for i in $(seq 1 15); do
                        if curl -fsS http://localhost:8080/ >/dev/null; then
                            echo "App is up."; exit 0
                        fi
                        echo "Waiting for app... ($i)"; sleep 4
                    done
                    echo "App did not become healthy in time."; exit 1
                '''
            }
        }
    }

    post {
        success { echo "Pipeline succeeded: ${env.DOCKER_IMAGE}:${env.BUILD_NUMBER}" }
        failure { echo 'Pipeline failed — check the stage logs above.' }
        always  { sh 'docker image prune -f || true' }
    }
}
