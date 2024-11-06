# 销售猫- WMS 正式环境部署指南

正式环境只会启动redis和Spring，数据库请使用外部数据库

## 端口详情
- 8080: wms_backend后端接口
- 6379: Redis

## 准备
1. 配置版本号
   
   构建前需要在系统环境中配置版本号（一般由Jenkins流水线构建设置）
      ```properties
      WMS_APP_VERSION=1.0.0-rc-c
      ```
   如果不需要，请删除docker-compose中以下行数，保持默认
   ```properties
      image: wms_backend:${WMS_APP_VERSION}
   ```
2. 必要配置文件

   请确保目录下拥有以下配置文件:
   - app.env: 后端接口调用配置
   - private.pem: jwt用私钥
   - public.pem: jwt用公钥
3. 其他文件说明

   一般情况下无需手动修改的文件，需加入Git跟踪，详情如下：
   - docker-compose.yml ： compose 构建脚本
   - Dockerfile: Spring 后端运行统一镜像，位置处于项目顶部目录下Dockerfile

## 构建过程
1. 再次确定以上配置准备已经就绪
2. 创建必要的docker 网络和卷
    ```bash
    docker volume create wms_spring_logs
    docker network create wms
    ```
3. 进入目录并开始构建
    ```bash
    cd .\deploy\prod
    docker-compose up -d --build 
    ```
4. 使用nginx反向代理8080， 参考如下
   ``` properties
   server {
       listen              443 ssl;
       http2 on;
       listen              [::]:443 ssl;
       server_name         api.wms.com;
   
       # SSL
       ssl_certificate     ../ssl/api.wms.com.pem;
       ssl_certificate_key ../ssl/api.wms.com-key.pem;
   
       client_max_body_size 100M;
   
       # location / {
       #     root   /usr/share/nginx/html;
       #     index  index.html index.htm;
       # }
   
       location / {
           proxy_set_header Host $host;
           proxy_set_header X-Real-IP $remote_addr;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header User-Agent $http_user_agent;
           proxy_set_header Cookie $http_cookie;
           proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
           proxy_set_header X-Forwarded-Proto $scheme;
           proxy_set_header X-Forwarded-Port $server_port;
   
           proxy_pass http://127.0.0.1:8080;
       }
   }
   
   server {
   listen      80;
   listen      [::]:80;
   server_name api.wms.com;
   return      301 https://api.wms.com$request_uri;
   }

   ```

## 配置详解
###  app.env
   ``` properties
   # 阿里云短信接口
   WMS_ALIYUN_ACCESS_ID=
   WMS_ALIYUN_ACCESS_SECRET=
   WMS_ALIYUN_ACCESS_SIGN_NAME=
   WMS_ALIYUN_ACCESS_TEMPLATE_CODE=
   
   # 邮件服务接口
   WMS_MAIL_HOST=
   WMS_MAIL_USERNAME=
   WMS_MAIL_PASSWORD=
   
   # 数据库配置
   # 数据库连接实例：jdbc:postgresql://192.168.31.162:5432/wms_dev
   WMS_DATASOURCE_URL=
   WMS_DATASOURCE_USERNAME=
   WMS_DATASOURCE_PASSWORD=
   ```


### jwt用密钥对
生成工程必要的密钥队，推荐使用openssl工具生成，参考如下
1. 私钥: `openssl genrsa -out private.pem`
2. 公钥 `openssl rsa -in privkey.pem -inform pem -pubout -out public.pem`

## 其他注意事项
docker-compose中可以指定后端版本号, 建议和打包的版本号保持一致:
   ```properties
    image: wms_backend:1.0.0-rc-1
   ```
