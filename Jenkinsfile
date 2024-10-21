def gv

pipeline {
    agent any
    stages {
        stage("init") {
            steps {
                script {
                    gv = load "script.groovy"
                }
            }
        }
        stage("increment") {
            steps {
                script {
                    echo "increment version"
                    gv.Increment()
                }
            }
        }
        stage("build jar") {
            steps {
                script {
                    echo "building jar"
                    gv.buildJar()
                }
            }
        }
        stage("build image") {
            steps {
                script {
                    echo "building image"
                    gv.buildImage()
                }
            }
        }
        stage("scan image") {
            steps {
                script {
                    echo "scanning image"
                    gv.scanImage()
                }
            }
        }
        stage("push image to repo") {
            steps {
                script {
                    echo "push image to repo"
                    gv.pushImage()
                }
            }
        }
    }   
}