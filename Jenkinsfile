pipeline {
    agent any

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install'
            }
        }
        stage('Build Docker Image') {
            steps {
                script {
                    docker.build("alinaveed1983/web-application")
                }
            }
        }
        stage('Deploy to Kubernetes') {
            steps {
                kubernetesDeploy(
                    configs: 'kubernetes/deployment.yaml,kubernetes/service.yaml',
                    kubeconfigId: 'your-kubeconfig-id'
                )
            }
        }
    }
}
