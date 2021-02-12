pipeline {
  agent any
    options {
        disableConcurrentBuilds()
  }
  stages {
    stage('Build and Test') {
       agent {
          docker {
            image 'maven:3.6.3-openjdk-15'
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
