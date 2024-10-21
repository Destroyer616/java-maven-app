def Increment() {
    echo "Increment the version..."
    sh 'mvn build-helper:parse-version versions:set \
        -DnewVersion=\\\${parsedVersion.majorVersion}.\\\${parsedVersion.minorVersion}.\\\${parsedVersion.nextIncrementalVersion} \
        versions:commit'
    def match = readFile('pom.xml') =~ '<version>(.+)</version>'
    def version = match[0][1]
    env.IMAGE_NAME = "$version-$BUILD_NUMBER"
} 

def buildJar() {
    sh 'mvn clean package'
}


def buildImage() {
    echo "building the docker image..."
    withCredentials([usernamePassword(credentialsId: 'docker-hosted', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh "echo $PASS | docker login -u $USER --password-stdin nexus.nexus.orb.local:8082"
        sh "docker build -t nexus.nexus.orb.local:8082/jenkins-maven-app:$IMAGE_NAME ."
    }
} 

def scanImage() {
    echo "scanning the image for critical vuln"
    sh "trivy image --exit-code 1 --severity CRITICAL --format json -o trivy_report.json nexus.nexus.orb.local:8082/jenkins-maven-app:$IMAGE_NAME"
}

def pushImage() {
    echo 'push the image to docker hosted repo..'
    sh "docker push nexus.nexus.orb.local:8082/jenkins-maven-app:$IMAGE_NAME"
} 

return this