@Library('my-shared-library') _

pipeline {
    agent any

    parameters {
        choice(name: 'action', choices: 'create\ndelete', description: 'Choose create/Delete')
        string(name: 'ImageName', description: "Name of the Docker build", defaultValue: 'web-application')
        string(name: 'ImageTag', description: "Tag of the Docker build", defaultValue: 'v1')
        string(name: 'DockerHubUser', description: "DockerHub username", defaultValue: 'alinaveed1983')
    }

    stages {
        stage('Git Checkout') {
            when { expression { params.action == 'create' } }
            steps {
                gitCheckout(
                    branch: "main",
                    url: "https://github.com/alinaveed1983/web-app1.git"
                )
            }
        }

        stage('Unit Test Maven') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    mvnTest()
                }
            }
        }

        stage('Integration Test Maven') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    mvnIntegrationTest()
                }
            }
        }

        stage('Static Code Analysis: SonarQube') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    def SonarQubeCredentialsId = 'sonarqube-api'
                    staticCodeAnalysis(SonarQubeCredentialsId)
                }
            }
        }

        stage('Quality Gate Status Check: SonarQube') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    def SonarQubeCredentialsId = 'sonarqube-api'
                    qualityGateStatus(SonarQubeCredentialsId)
                }
            }
        }

        stage('Maven Build') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    mvnBuild()
                }
            }
        }

        stage('Docker Image Build') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerBuild("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }

        stage('Docker Image Scan: Trivy') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerImageScan("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }

        stage('Docker Image Push: DockerHub') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerImagePush("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }

        stage('Docker Image Cleanup: DockerHub') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    dockerImageCleanup("${params.ImageName}", "${params.ImageTag}", "${params.DockerHubUser}")
                }
            }
        }

        stage('Deploy to Kubernetes') {
            when { expression { params.action == 'create' } }
            steps {
                script {
                    sh 'kubectl apply -f kubernetes/deployment.yaml --v=9'
                    sh 'kubectl apply -f kubernetes/service.yaml --v=9'
                }
            }
        }

        stage('Cleanup Deployments') {
            when { expression { params.action == 'delete' } }
            steps {
                script {
                    sh 'kubectl delete -f kubernetes/deployment.yaml --v=9'
                    sh 'kubectl delete -f kubernetes/service.yaml --v=9'
                }
            }
        }
    }
}
