@Library('my-shared-library') _

pipeline {
    agent any

    parameters {
        choice(name: 'action', choices: 'create\ndelete', description: 'Choose create/Delete')
        string(name: 'ImageName', description: "Name of the Docker build", defaultValue: 'web-application')
        string(name: 'ImageTag', description: "Tag of the Docker build", defaultValue: 'v1')
        string(name: 'DockerHubUser', description: "DockerHub username", defaultValue: 'alinaveed1983')
    }

    environment {
        K8S_SA_TOKEN = credentials('k8s-sa-token')  // Use the ID you provided in Jenkins
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
                    withEnv(["KUBECONFIG=/tmp/kubeconfig"]) {
                        sh '''
                        echo "
                        apiVersion: v1
                        kind: Config
                        clusters:
                        - cluster:
                            certificate-authority: /root/.minikube/ca.crt
                            server: https://192.168.49.2:8443
                          name: minikube
                        contexts:
                        - context:
                            cluster: minikube
                            namespace: default
                            user: minikube
                          name: minikube
                        current-context: minikube
                        users:
                        - name: minikube
                          user:
                            token: ${K8S_SA_TOKEN}
                        " > /tmp/kubeconfig
                        '''
                        sh 'kubectl apply -f kubernetes/deployment.yaml'
                        sh 'kubectl apply -f kubernetes/service.yaml'
                    }
                }
            }
            post {
                failure {
                    script {
                        echo "Deployment to Kubernetes failed. Collecting logs..."
                        sh 'kubectl get pods --all-namespaces'
                        sh 'kubectl describe pods'
                        sh 'kubectl describe services'
                    }
                }
            }
        }

        stage('Cleanup Deployments') {
            when { expression { params.action == 'delete' } }
            steps {
                script {
                    withEnv(["KUBECONFIG=/tmp/kubeconfig"]) {
                        sh '''
                        echo "
                        apiVersion: v1
                        kind: Config
                        clusters:
                        - cluster:
                            certificate-authority: /root/.minikube/ca.crt
                            server: https://192.168.49.2:8443
                          name: minikube
                        contexts:
                        - context:
                            cluster: minikube
                            namespace: default
                            user: minikube
                          name: minikube
                        current-context: minikube
                        users:
                        - name: minikube
                          user:
                            token: ${K8S_SA_TOKEN}
                        " > /tmp/kubeconfig
                        '''
                        sh 'kubectl delete -f kubernetes/deployment.yaml'
                        sh 'kubectl delete -f kubernetes/service.yaml'
                    }
                }
            }
        }
    }
}
