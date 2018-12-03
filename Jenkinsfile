#!/usr/bin/env groovy
def dockerImage

def tagMatchRules = [
        [
                meTypes: [
                        [meType: 'SERVICE']
                ],
                tags   : [
                        [context: 'CONTEXTLESS', key: 'Frontend']
                ]
        ], [
                meTypes: [
                        [meType: 'SERVICE']
                ],
                tags   : [
                        [context: 'CONTEXTLESS', key: 'Database']
                ]
        ]
]

pipeline {
    agent {
        label 'docker'
    }
    options {
        ansiColor('xterm')
        timestamps()
        timeout(30)
        disableConcurrentBuilds()
        buildDiscarder logRotator(numToKeepStr: '25')
    }
    triggers {
        cron '@daily'
    }

    stages {
        stage('Maven Build') {
            steps {
                script {
                    docker.image('maven:3-jdk-8-slim').inside {
                        sh 'mvn clean package --batch-mode'
                    }
                }
//                publishCoverage adapters: [jacocoAdapter('target/jacoco.exec')]
//                findbugs pattern: '**/target/findbugsXml.xml'
//                checkstyle pattern: '**/target/checkstyle-result.xml'
//                junit allowEmptyResults: true, testResults: '**/target/surefire-reports/**/*.xml'
                archiveArtifacts artifacts: '**/target/*.jar,**/target/*.war', fingerprint: true
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    dockerImage = docker.build('spring-petclinic:latest')
                }
            }
        }
        stage('Deploy to Staging Server') {
            steps {
                createDynatraceDeploymentEvent(envId: 'Dynatrace Demo Environment', tagMatchRules: tagMatchRules) {
                    sh 'docker-compose down'
                    sh 'docker-compose up -d'
                }
            }
        }
        stage('Performance Test') {
            steps {
                recordDynatraceSession(envId: 'Dynatrace Demo Environment', testCase: 'loadtest', tagMatchRules: tagMatchRules) {
                    performanceTest(readFile('performanceTest.json'))
                }
                perfSigDynatraceReports envId: 'Dynatrace Demo Environment', specFile: 'specfile.json', nonFunctionalFailure: 2
            }
        }
        stage('Docker Push') {
            steps {
                script {
                    docker.withRegistry('https://hub.docker.com', 'dockerHubCredentials') {
                        dockerImage.push('latest')
                    }
                }
            }
        }
        stage('deploy to Production') {
            steps {
                echo 'deploy to Production!'
            }
        }
    }
    post {
        always {
            step([$class: 'Mailer', notifyEveryUnstableBuild: true, recipients: 'notify@me', sendToIndividuals: false])
        }
    }
}
