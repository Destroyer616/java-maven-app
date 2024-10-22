def Increment() {
    sh "mvn clean install"
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
    sh "trivy image --exit-code 1 --severity CRITICAL --format table -o report.txt nexus.nexus.orb.local:8082/jenkins-maven-app:$IMAGE_NAME"
}

def pushImage() {
    echo 'push the image to docker hosted repo..'
    sh "docker push nexus.nexus.orb.local:8082/jenkins-maven-app:$IMAGE_NAME"
} 

def commitChange() {
    withCredentials([usernamePassword(credentialsId: 'github_credential', passwordVariable: 'PASS', usernameVariable: 'USER')]) {
        sh 'git config user.email "saikiran.reddy916@gmail.com"'
        sh "git config user.name $USER"

        sh "git remote set-url origin https://${USER}:${PASS}@github.com/Destroyer616/java-maven-app"
        sh 'git add .'
        sh "git commit -m 'ci: version bump to version:$IMAGE_NAME"
        sh 'git push origin main'
    }
}

def trivyScanEmail() {
    emailext(
                    subject: "Jenkins: Trivy Scan of nexus.nexus.orb.local:8082/jenkins-maven-app:$IMAGE_NAME completed  - Critical Vulnerabilities Detected",
                    body: """\
                        Hi Team,

                        The Trivy scan has detected critical vulnerabilities in the jenkins-maven-app:$IMAGE_NAME image.
                        Please find the attached report for more details.

                        Regards,
                        deadpool
                    """,
                    to: 'sa-ki616@proton.me',
                    attachmentsPattern: 'report.txt'
                )
}

return this