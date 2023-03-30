pipeline {
  environment {
    DOCKER_REGISTRY='docker.repos.blinfo.se'
  }
  agent any
    options {
        disableConcurrentBuilds()
  }
  stages {
    stage('Build and Test') {
       agent {
          docker {
            image "${DOCKER_REGISTRY}/blinfo/maven:3.8.5-jdk-17"
          }
       }
       steps {
            sh "mvn clean package"
       }
       post {
           always {
            sh "touch target/surefire-reports/*.xml"
            junit 'target/surefire-reports/*.xml'
           }
       }
    }
    stage('Deploy Artifacts') {
        agent {
          docker {
            image "${DOCKER_REGISTRY}/blinfo/maven:3.8.5-jdk-17"
            args '-e MAVEN_CONFIG=/home/jenkins/.m2'
          }
        }
        steps {
            sh "mvn -B deploy -DskipTests"
       }
    }
  }
  post {
    failure {
      script {
        COMMITER_EMAIL = sh(returnStdout: true, script: 'git log -1 --pretty=format:"%ae"')
      }
      emailext body: '${JELLY_SCRIPT,template="bl"}', subject: "${JOB_NAME} build ${BUILD_NUMBER} ${currentBuild.currentResult}", to: "${COMMITER_EMAIL}"
    }
  }
}
