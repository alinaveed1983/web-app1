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
                            certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCakNDQWU2Z0F3SUJBZ0lCQVRBTkJna3Fo
a2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwdGFXNXAKYTNWaVpVTkJNQjRYRFRJME1EY3lN
VEV3TkRVeE9Gb1hEVE0wTURjeU1ERXdORFV4T0Zvd0ZURVRNQkVHQTFVRQpBeE1LYldsdWFXdDFZ
bVZEUVRDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTVZmCkVvWmll
cGNweWJQb21NRzBmdHVycm9wSUN4VHFWZUZWdWJudDdIcTNidzJvenJtY1lXOVhpOTJPSVpHTHBh
LzEKdm1OdUsra3E2NUk5QzhIVThIdDdtTkE0TDBLMUg4N09FUXBwK3ZDb08yczNUb05nT1ZZSUlw
TWUvWlhBS0JzYQp4a1pYZWN1M1VBMGwveHJKTDlYemcxYmE2LytjNzN3SG9FWktRaHY2SkNDUUQ2
QUhNanFYU2VWZEdRUWFEWEV6CjNrdTNIekgvZk9VSXZMalduNDQ4ejRwYlhiQmVYRnY1SHNxa1FJ
dWViQnhTYWx4Zk93UVZqSVlJWUpzZXJselEKeEhSMzVKYVcxZ0ZPenVJdnlLNG9uaTN6bW9qL2h0
dmNpSW4wNW1Ia3BCTkFPTi9HaG5HaXFSS0dlbDMrd2Q4TAo2TUdTTzlZOU0zL3lGWEdGQ3E4Q0F3
RUFBYU5oTUY4d0RnWURWUjBQQVFIL0JBUURBZ0trTUIwR0ExVWRKUVFXCk1CUUdDQ3NHQVFVRkJ3
TUNCZ2dyQmdFRkJRY0RBVEFQQmdOVkhSTUJBZjhFQlRBREFRSC9NQjBHQTFVZERnUVcKQkJTN0x5
SCtXdDFiUVQzd2dzOWR5VE1IU0NBdW16QU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFJSm5QVjQ1
YQpsNFBNTEM0dkZHUHlFaVA5K2tjRFE0ejJiSnl2NGY4anE1ZEZBdkNqK1ZuQXAzZVltc3FPamlz
bG5sRnp4cVQ5Ck91ZUpNemZqRHdtc0g0OWpIVXhuK1RZTGxVZG5pVXJvMjZqQnBqTlFmdFM2VFhQ
cTNYbWMyMUd3SGsrMUxZV3UKM0Z4TzdDU0Foc2I5VU8wc2UxUC92OGFOTVNMOE8yb3JZcWY0MVpk
R3A3QkUzdlJVdnhKbDJzQVUweUpmUmpaVgpXOGpFZlZuTjhJK2I1bG1iOXNaVWpKYTZNTVZhcldK
OXltd3I2WGk3YURzNmFXMy9tWjdobHJjRGxuY0ZxVnYrCmw4ZDk3MHJlNFR4MFhNSjBCa3U1NWFq
SFlMaitLcC9tSW9LU1pwZ0dMamdYQW84Vk1zWExVLzRjelpHU1NmTFMKMkpiYlhYZ3dibWtFWFE9
PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
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
                            certificate-authority-data: LS0tLS1CRUdJTiBDRVJUSUZJQ0FURS0tLS0tCk1JSURCakNDQWU2Z0F3SUJBZ0lCQVRBTkJna3Fo
a2lHOXcwQkFRc0ZBREFWTVJNd0VRWURWUVFERXdwdGFXNXAKYTNWaVpVTkJNQjRYRFRJME1EY3lN
VEV3TkRVeE9Gb1hEVE0wTURjeU1ERXdORFV4T0Zvd0ZURVRNQkVHQTFVRQpBeE1LYldsdWFXdDFZ
bVZEUVRDQ0FTSXdEUVlKS29aSWh2Y05BUUVCQlFBRGdnRVBBRENDQVFvQ2dnRUJBTVZmCkVvWmll
cGNweWJQb21NRzBmdHVycm9wSUN4VHFWZUZWdWJudDdIcTNidzJvenJtY1lXOVhpOTJPSVpHTHBh
LzEKdm1OdUsra3E2NUk5QzhIVThIdDdtTkE0TDBLMUg4N09FUXBwK3ZDb08yczNUb05nT1ZZSUlw
TWUvWlhBS0JzYQp4a1pYZWN1M1VBMGwveHJKTDlYemcxYmE2LytjNzN3SG9FWktRaHY2SkNDUUQ2
QUhNanFYU2VWZEdRUWFEWEV6CjNrdTNIekgvZk9VSXZMalduNDQ4ejRwYlhiQmVYRnY1SHNxa1FJ
dWViQnhTYWx4Zk93UVZqSVlJWUpzZXJselEKeEhSMzVKYVcxZ0ZPenVJdnlLNG9uaTN6bW9qL2h0
dmNpSW4wNW1Ia3BCTkFPTi9HaG5HaXFSS0dlbDMrd2Q4TAo2TUdTTzlZOU0zL3lGWEdGQ3E4Q0F3
RUFBYU5oTUY4d0RnWURWUjBQQVFIL0JBUURBZ0trTUIwR0ExVWRKUVFXCk1CUUdDQ3NHQVFVRkJ3
TUNCZ2dyQmdFRkJRY0RBVEFQQmdOVkhSTUJBZjhFQlRBREFRSC9NQjBHQTFVZERnUVcKQkJTN0x5
SCtXdDFiUVQzd2dzOWR5VE1IU0NBdW16QU5CZ2txaGtpRzl3MEJBUXNGQUFPQ0FRRUFJSm5QVjQ1
YQpsNFBNTEM0dkZHUHlFaVA5K2tjRFE0ejJiSnl2NGY4anE1ZEZBdkNqK1ZuQXAzZVltc3FPamlz
bG5sRnp4cVQ5Ck91ZUpNemZqRHdtc0g0OWpIVXhuK1RZTGxVZG5pVXJvMjZqQnBqTlFmdFM2VFhQ
cTNYbWMyMUd3SGsrMUxZV3UKM0Z4TzdDU0Foc2I5VU8wc2UxUC92OGFOTVNMOE8yb3JZcWY0MVpk
R3A3QkUzdlJVdnhKbDJzQVUweUpmUmpaVgpXOGpFZlZuTjhJK2I1bG1iOXNaVWpKYTZNTVZhcldK
OXltd3I2WGk3YURzNmFXMy9tWjdobHJjRGxuY0ZxVnYrCmw4ZDk3MHJlNFR4MFhNSjBCa3U1NWFq
SFlMaitLcC9tSW9LU1pwZ0dMamdYQW84Vk1zWExVLzRjelpHU1NmTFMKMkpiYlhYZ3dibWtFWFE9
PQotLS0tLUVORCBDRVJUSUZJQ0FURS0tLS0tCg==
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
