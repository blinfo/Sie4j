pipeline {
  agent any
    options {
        disableConcurrentBuilds()
  }
  stages {
    stage('Build and Test') {
       agent {
          docker {
            image 'maven:3.6.1-openjdk-11'
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
            image 'blinfo/maven:3.6.1-jdk-8-alpine'
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
