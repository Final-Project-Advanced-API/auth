pipeline {
    agent any

    environment {
        IMAGE = "soklay515/user-service"
        DOCKER_IMAGE = "${IMAGE}:${BUILD_NUMBER}"
        DOCKER_CREDENTIALS_ID = 'dockerhub-token'
        GIT_MANIFEST_REPO = "github.com/12-Generation-Advanced-Course-Project/Stacknote-Manifest.git"
        GIT_BRANCH = "user-service"
        MANIFEST_REPO = "Stacknote-Manifest"
        MANIFEST_FILE_PATH = "manifest/deployment.yaml"
        GIT_CREDENTIALS_ID = 'Stacknote'
        ARGOCD_APP_NAME = 'user-service'
        ARGOCD_SERVER = 'https://163.47.8.124'
        ARGOCD_TOKEN = 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhcmdvY2QiLCJzdWIiOiJhZG1pbjphcGlLZXkiLCJuYmYiOjE3MjkxMjk0MTgsImlhdCI6MTcyOTEyOTQxOCwianRpIjoiYzI4YzE2NzctODQ4MC00NDYxLWEwYzAtNTU3NThiMGFlNmZhIn0.-BXA3rn_R4kRtCnl0UhClX1dAjPfeLG1nKZvyGdzg8s' // Make sure this token is on a single line
        TELEGRAM_BOT_TOKEN = '7997280208:AAHi1EBBIMt8TPMvyPRqFw45q9Ua9I9amCw'
        TELEGRAM_CHAT_ID = '-4542876022'
    }

    stages {
        stage("Checkout") {
            steps {
                echo "🚀 Running checkout..."
                sh 'docker image prune --all --force'
                sh 'pwd'
                sh 'ls -alh'
            }
        }

        stage("Build and Push Docker Image") {
            steps {
                script {
                    echo "🚀 Building docker image..."
                    sh "docker build -t ${DOCKER_IMAGE} ."
                    sh "docker images | grep -i ${IMAGE}"

                    echo "🚀 Logging in to Docker Hub..."
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                        sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                    }

                    echo "🚀 Pushing the image to Docker Hub..."
                    sh "docker push ${DOCKER_IMAGE}"
                }
            }
        }

        stage("Clone Manifest Repository") {
            steps {
                script {
                    echo "🚀 Cloning the manifest repository..."
                    withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                        sh '''
                            echo "Removing existing repository if exists..."
                            if [ -d "${MANIFEST_REPO}" ]; then
                                rm -rf ${MANIFEST_REPO}
                            fi
                            echo "Cloning the manifest repository..."
                            git clone -b ${GIT_BRANCH} https://${GIT_USER}:${GIT_PASS}@${GIT_MANIFEST_REPO} ${MANIFEST_REPO}
                        '''
                    }
                }
            }
        }

        stage("Push Changes to Manifest") {
            steps {
                script {
                    dir("${MANIFEST_REPO}") {
                        withCredentials([usernamePassword(credentialsId: GIT_CREDENTIALS_ID, passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                            echo "🚀 Updating the manifest file with the new image..."
                            sh '''
                                # Check contents before updating
                                cat ${MANIFEST_FILE_PATH}

                                # Update the image in the manifest
                                sed -i "s|image: ${IMAGE}:.*|image: ${DOCKER_IMAGE}|" ${MANIFEST_FILE_PATH}

                                git config --global user.name "soklaymeng"
                                git config --global user.email "mengsoklay2222@gmail.com"
                                
                                git add .
                                if ! git diff --cached --quiet; then
                                    git commit -m "Update image to ${DOCKER_IMAGE}"
                                    git push https://${GIT_USER}:${GIT_PASS}@${GIT_MANIFEST_REPO}
                                else
                                    echo "No changes to commit."
                                fi
                            '''
                        }
                    }
                }
            }
        }

        stage("Trigger ArgoCD Sync and Notify") {
            steps {
                script {
                    echo "🚀 Triggering ArgoCD Sync..."
                    def response = sh(
                        script: """
                            curl -k -s -X POST ${ARGOCD_SERVER}/api/v1/applications/${ARGOCD_APP_NAME}/sync \
                                -H 'Authorization: Bearer ${ARGOCD_TOKEN}' \
                                -H 'Content-Type: application/json'
                        """,
                        returnStdout: true
                    ).trim()
                    echo "ArgoCD Response: ${response}"
                }
            }
        }
    }

    post {
        success {
            script {
                echo "🚀 Build successful, notifying via Telegram..."
                sh """
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d parse_mode="HTML" \
                    -d text="<b>Stage</b>: ✅ Authentication-Service \
                    %0A<b>Status</b>: This authentication service was built successfully 🚀"
                """
            }
        }

        failure {
            script {
                echo "🚨 Build failed, notifying via Telegram..."
                sh """
                curl -s -X POST https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage \
                    -d chat_id=${TELEGRAM_CHAT_ID} \
                    -d parse_mode="HTML" \
                    -d text="<b>Stage</b>: ❌ Authentication-Service \
                    %0A<b>Status</b>: This authentication service build failed 💥"
                """
            }
        }

        always {
            echo "🚀 Cleanup after build..."
            cleanWs() // Cleans up the workspace after build
        }
    }
}
