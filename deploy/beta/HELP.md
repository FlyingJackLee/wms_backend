# 销售猫- WMS Beta环境部署指南

## 对外端口详情
   - 8081: wms_backend后端接口
   - 5431: Postgres Database
   - 5432: adminer数据库管理接口
   - 6371: Redis

## 准备
请确保目录下拥有以下配置文件:
- beta.env: 后端接口调用配置
- db.env：postgres密码配置
- private.pem: jwt用私钥
- public.pem: jwt用公钥

其他文件无需手动创建，需加入Git跟踪，详情如下：
- docker-compose.yml ： compose 构建脚本
- pg.Dockerfile : 创建pg镜像文件（包含初始化脚本过程）
- schema.sql: 数据库初始化脚本（应当与项目原始脚本一致src/main/resources/schema.sql）
- Dockerfile: Spring 后端运行统一镜像，位置处于项目顶部目录下Dockerfile

## 构建过程
1. 再次确定以上配置准备已经就绪
2. 创建必要的docker 网络和卷
    ```bash
    docker volume create wms_beta_db_data
    docker volume create wms_beta_spring_logs
    docker network create wms-beta
    ```
3. 进入目录并开始构建启动
    ```bash
    cd .\deploy\beta
    docker-compose up -d --build 
    ```
4. 移除容器
   ```bash
   docker-compose down
    ```

## 配置详解
###  beta.env
    # 阿里云短信接口
    WMS_ALIYUN_ACCESS_ID=
    WMS_ALIYUN_ACCESS_SECRET=
    WMS_ALIYUN_ACCESS_SIGN_NAME=
    WMS_ALIYUN_ACCESS_TEMPLATE_CODE=
    
    # 邮件服务接口
    WMS_MAIL_HOST=
    WMS_MAIL_USERNAME=
    WMS_MAIL_PASSWORD=`

###  beta.env
    # 配置数据库和spring datasource密码，两者应该一样, beta环境建议随机生成
    POSTGRES_PASSWORD=B9aM5VRad8p2uPPF3AVb
    WMS_DATASOURCE_PASSWORD=B9aM5VRad8p2uPPF3AVb

### jwt用密钥对
生成工程必要的密钥对，推荐使用openssl工具生成，参考如下
1. 私钥: `openssl genrsa -out private.pem`
2. 公钥 `openssl rsa -in private.pem -inform pem -pubout -out public.pem`
