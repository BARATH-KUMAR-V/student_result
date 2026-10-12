pipeline {
    agent any

    tools {
        // Ensure Maven and JDK tools are configured in Jenkins 'Global Tool Configuration'
        maven 'Maven-3.9'
        jdk 'JDK-21'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }

    environment {
        APP_NAME = 'student-result-portal'
    }

    stages {
        // Stage 1: Checkout Code from SCM / Git
        stage('Checkout') {
            steps {
                echo "===> [1/5] Checking out source code from Git repository..."
                checkout scm
            }
        }

        // Stage 2: Compile the Spring Boot Application
        stage('Build') {
            steps {
                echo "===> [2/5] Compiling Java source files with Maven..."
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        // Stage 3: Run Unit Tests & Collect Reports
        stage('Test') {
            steps {
                echo "===> [3/5] Running unit and integration tests..."
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    echo "Publishing JUnit test results..."
                    junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
                }
            }
        }

        // Stage 4: Package into Executable Spring Boot JAR
        stage('Package') {
            steps {
                echo "===> [4/5] Packaging application into executable JAR..."
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
        }

        // Stage 5: Archive Build Artifacts
        stage('Archive') {
            steps {
                echo "===> [5/5] Archiving generated JAR artifact..."
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true, allowEmptyArchive: false
            }
        }
    }

    post {
        success {
            echo "=========================================================="
            echo " Pipeline Execution Completed Successfully! "
            echo " Artifact: target/student-result-portal-1.0.0.jar is ready."
            echo "=========================================================="
        }
        failure {
            echo "=========================================================="
            echo " Pipeline FAILED! Please inspect stage logs above.       "
            echo "=========================================================="
        }
    }
}
