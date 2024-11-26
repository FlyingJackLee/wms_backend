pipeline {
    // 请确保agent上配置了
    // 1. docker
    // 2. docker-compose
    // 3. jdk21
    // 4. maven
    // 5. git
    agent{
        label 'prod'
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
    }

    tools{
        maven 'M3'
        jdk 'jdk21'
    }

    environment {
        TZ = 'Asia/Shanghai'
    }

    stages {
        stage('Preparation'){
            steps {
                git branch: 'dev-jenkins', credentialsId: 'gitee', url: 'git@gitee.com:zuminli/wms_backend.git'

                /*
                    下载配置文件
                 */
                dir('temp'){
                    checkout scmGit(branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[credentialsId: 'codeup-read', url: 'git@codeup.aliyun.com:63a434c1aa32314b151eef46/wms/wms_configs.git']])
                }
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
                // 构建测试用环境
                dir('deploy/test') {
                    // 生成测试用秘钥对
                    sh "openssl genrsa -out private.pem"
                    sh "openssl rsa -in private.pem -inform pem -pubout -out public.pem"

                    // 先尝试卸载，再构建，防止容器冲突
                    sh "docker-compose down"
                    sh "docker-compose up -d --build"
                }

                sh 'mvn clean package'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }

            // 删除临时文件
            post{
                always {
                    dir('deploy/test') {
                        sh "rm *.pem"
                        sh "docker-compose down"
                    }

                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }


        stage('Deploy'){
            steps {

            }
        }
    }

    post {
        always {
            cleanWs cleanWhenFailure: false, cleanWhenNotBuilt: false, cleanWhenUnstable: false
        }
    }
}