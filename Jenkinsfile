pipeline {
    agent any

    environment {
        IMAGE = "soklay515/auth-service"
        DOCKER_IMAGE = "${IMAGE}:${BUILD_NUMBER}"
        DOCKER_CREDENTIALS_ID = 'dockerhub-token'
        GIT_MANIFEST_REPO = "github.com/12-Generation-Advanced-Course-Project/Stacknote-Manifest.git"
        GIT_BRANCH = "user-service"
        MANIFEST_REPO = "Stacknote-Manifest" 
        MANIFEST_FILE_PATH = "manifest/deployment.yaml"
        GIT_CREDENTIALS_ID = 'Stacknote'
    }

    stages {

        stage("checkout") {
            steps {
            echo "🚀🚀🚀🚀 Running..."
            echo "Running on $NODE_NAME"
            echo "$BUILD_NUMBER"
            sh ' docker image prune --all '
            sh 'pwd'
            sh 'ls'
          }
        }


        stage("build and push docker image") {

            steps {
                script {
                    echo "🚀 Building docker image..."
                    sh ' docker build -t ${DOCKER_IMAGE} .'
                    sh ' docker images | grep -i ${IMAGE} '
                    
                    echo "🚀 Log in Docker hub using Jenkins credentials..."
                    withCredentials([usernamePassword(credentialsId: DOCKER_CREDENTIALS_ID, passwordVariable: 'DOCKER_PASS', usernameVariable: 'DOCKER_USER')]) {
                      sh 'echo "${DOCKER_PASS} ${DOCKER_USER}" '
                      sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                    }
                    echo "🚀 Pushing the image to Docker hub"
                    sh 'docker push ${DOCKER_IMAGE}'
                    
                }
            }
        }

        stage("Cloning the manifest file") {
            steps {
                 script {
                    dir("${MANIFEST_REPO}") {
                        withCredentials([usernamePassword(credentialsId: 'Stacknote', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                        sh "pwd"
                        sh "ls -l"
                        echo "🚀 Checking if the manifest repository exists and removing it if necessary..."
                        sh '''
                            if [ -d "${MANIFEST_REPO}" ]; then
                                echo "🚀 ${MANIFEST_REPO} exists, removing it..."
                                rm -rf ${MANIFEST_REPO}
                            fi
                        '''
                        echo "🚀 Updating the image of the Manifest file..."
                        sh "git clone -b ${GIT_BRANCH} https://${GIT_USER}:${GIT_PASS}@${GIT_MANIFEST_REPO} ${MANIFEST_REPO}"
                        sh "ls -l"
                        }
                    }
                 }
            }
        }


        // stage("Updating the manifest file") {
        //     steps {
        //         script {
        //             echo "🚀 Update the image in the deployment manifest..."
        //             sh """
        //             pwd 
        //             ls Stacknote-Manifest/
        //             sed -i 's|image: ${IMAGE}:.*|image: ${DOCKER_IMAGE}|' ${MANIFEST_REPO}/${MANIFEST_REPO}/${MANIFEST_FILE_PATH}
        //             """
        //         }
        //     }
        // }

        stage("push changes to the manifest") {
            steps {
                script {
                    dir("${MANIFEST_REPO}") {
                        withCredentials([usernamePassword(credentialsId: 'Stacknote', passwordVariable: 'GIT_PASS', usernameVariable: 'GIT_USER')]) {
                            sh """
                            
                            echo "🚀 Update the image in the deployment manifest..."
                            pwd 
                            ls Stacknote-Manifest/
                            sed -i 's|image: ${IMAGE}:.*|image: ${DOCKER_IMAGE}|' ${MANIFEST_REPO}/${MANIFEST_FILE_PATH}
                            cd ${MANIFEST_REPO}
                            ls -l
                            git config --global user.name "soklaymeng"
                            git config --global user.email "mengsoklay2222@gmail.com"
                            echo "🚀 Checking..."
                            git branch 
                            pwd 
                            echo "🚀 Start pushing to manifest repo"  
                            git add .
                            git commit -m "Update image to ${DOCKER_IMAGE}"
                            git push https://${GIT_USER}:${GIT_PASS}@${GIT_MANIFEST_REPO}
                            """
                        }
                    }
                }
            }
        }
        

    }
}
