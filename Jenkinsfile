pipeline {
    agent any
    environment {
        IMAGE = "soklay515/auth-service"
        DOCKER_IMAGE = "${IMAGE}:${BUILD_NUMBER}"
        DOCKER_HUB_CREDENTIAL = "Stacknote"
        MANIFEST_REPO = "manifest-repo"
        GIT_MANIFEST_REPO = "https://github.com/12-Generation-Advanced-Course-Project/Stacknote-Auth-Service.git"
        GIT_CREDENTIALS_ID = "git-token"
        MANIFEST_FILE_PATH = "manifest/deployment.yaml"
    }
    stages {

        stage("cleanup") {
            steps {
                // sh " mvn clean install"
                // sh " docker image prune -a "
                echo "hello jenkins"
            }
        }

        stage ("build") {
            steps {
                echo "Hello world !!"
                sh " docker build -t ${DOCKER_IMAGE} ."
                sh " docker images | grep -i ${IMAGE} "
            }
        }

        stage ("push image to docker hub") {
            steps {
                script {
                     withCredentials([usernamePassword(credentialsId: DOCKER_HUB_CREDENTIAL, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                      sh 'echo "${DOCKER_PASS} ${DOCKER_USER}" '
                      sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    }
                    echo "🚀 Pushing the image to Docker hub"
                    sh 'docker push ${DOCKER_IMAGE}'
                }
               
            }
        }

        stage ("clone manifest file") {
             steps {
                    sh "pwd"
                    sh "ls -l"
                    sh '''
                    if [ -d "${MANIFEST_REPO}" ]; then
                        echo "🚀 ${MANIFEST_REPO} exists, removing it..."
                        rm -rf ${MANIFEST_REPO}
                    fi
                    '''
                    echo "🚀 Updating the image of the Manifest file..."
                    sh "git clone -b main ${GIT_MANIFEST_REPO} ${MANIFEST_REPO}"
                    sh "ls -l"
             }
        }
        
        stage("Updating the manifest file") {
            steps {
                script {
                    echo "🚀 Update the image in the deployment manifest..."
                    sh """
                    sed -i 's|image: soklay515/auth-service.*|image: ${DOCKER_IMAGE}|' ${MANIFEST_REPO}/${MANIFEST_FILE_PATH}
                    """
                }
            }
        }

         stage("push changes to the manifest") {
            steps {
                script {
                    dir("${MANIFEST_REPO}") {
                        withCredentials([usernamePassword(credentialsId: 'git-token', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                            sh """
                            git config --global user.name "soklaymeng"
                            git config --global user.email "mengsoklay2222@gmail.com"
                            echo "🚀 Checking..."
                            git branch
                            ls -l 
                            pwd 
                            echo "🚀 Start pushing to manifest repo"
                            git add ${MANIFEST_FILE_PATH}
                            git commit -m "update images to ${DOCKER_IMAGE}"
                            git push https://${GIT_USER}:${GIT_PASS}@github.com/12-Generation-Advanced-Course-Project/Stacknote-Auth-Service
                            """
                        }
                    }
                }
            }
        }

        
        
    }
}
