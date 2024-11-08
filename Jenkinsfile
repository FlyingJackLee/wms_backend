pipeline {
    agent any

    options {
        timeout(time: 10, unit: 'MINUTES')
    }

    tools {
        maven 'M3'
        jdk 'jdk21'
    }

    environment {
        TZ = 'Asia/Shanghai'
    }

    stages {
        stage('Checkout') {
            steps {
                // Checkout the code from the repository
                checkout scm
            }
        }

        /*
          构建阶段：
              无论什么环境都应该使用这个步骤进行构建和测试
              注意需要在构建机上安装docker和docker-compose
              具体请参考jenkins_install
        */
        stage('Building') {
            environment {
                JWT_PRIVATE_KEY_PATH = './deploy/test/private.pem'
                JWT_PUBLIC_KEY_PATH = './deploy/test/public.pem'
            }

            steps {
                dir('deploy/test') {
                    // 生成测试用秘钥对
                    sh "openssl genrsa -out private.pem"
                    sh "openssl rsa -in private.pem -inform pem -pubout -out public.pem"

                    // 构建测试用环境
                    sh "docker-compose up -d --build"
                }

                sh 'mvn clean package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }

            // 删除临时文件
            post{
                always {
                    junit '**/target/*.xml'

                    dir('deploy/test') {
                        sh "rm *.pem"
                        sh "docker-compose down"
                    }
                }
            }
        }

        /*
          Beta部署阶段：
              请确保允许环境上包含docker和docker-compose
        */
        stage('Deploy on beta'){
            agent{
                label 'tencent-beta'
            }
            when{
                beforeAgent true
                branch 'dev'
            }
            steps {
                dir('deploy/beta') {
                    echo '构建beta环境用秘钥对'
                    sh "openssl genrsa -out private.pem"
                    sh "openssl rsa -in private.pem -inform pem -pubout -out public.pem"

                    echo '开始部署'
                    sh "docker-compose up -d --build"
                }
            }
        }
    }
}