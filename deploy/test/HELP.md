# 销售猫- WMS 测试环境部署指南

## 对外端口详情
   - 5433: Postgres Database
   - 6373: Redis

## 文件说明
测试时请确保host上已经设置好:
- private.pem: jwt用私钥
- public.pem: jwt用公钥

并将路径加入了环境变量：
- JWT_PRIVATE_KEY_PATH
- JWT_PUBLIC_KEY_PATH"

其他文件无需手动创建，需加入Git跟踪，详情如下：
- docker-compose.yml ： compose 构建脚本(密码端口等请和application-test.properties一致)
- pg.Dockerfile : 创建pg镜像文件（包含初始化脚本过程）
- schema.sql: 数据库初始化脚本
- Dockerfile: Spring 后端运行统一镜像，位置处于项目顶部目录下Dockerfile

## 构建过程
1. 再次确定以上配置准备已经就绪
2. 进入目录并开始构建启动
    ```bash
    cd .\deploy\test
    docker-compose up -d --build 
    ```
3. 回到项目主目录开始测试
   ```bash
   mvn test 
    ```
4. 测试完后，移除容器
   ```bash
   docker-compose down
    ```

## 配置详解
### 测试用properties请参考
```properties
spring.jackson.time-zone=GMT+8

# database
spring.datasource.url=jdbc:postgresql://127.0.0.1:5433/wms_test
spring.datasource.username=wms_test
spring.datasource.password=dI1sP7aU5

# 短信发送密钥
aliyun.access.id=
aliyun.access.secret=
aliyun.access.signName=
aliyun.access.templateCode=

spring.sql.init.mode=always
spring.sql.init.encoding=UTF-8
spring.sql.init.username=wms_test
spring.sql.init.password=dI1sP7aU5
spring.sql.init.schema-locations=classpath:schema-test.sql
spring.sql.init.data-locations=classpath:data-test.sql
spring.sql.init.continue-on-error=false

# mybatis
mybatis.mapper-locations=classpath:/mapper/*.xml
mybatis.configuration.map-underscore-to-camel-case=true

# i18
spring.messages.basename= i18n/message
spring.messages.encoding= UTF-8

# Redis
spring.data.redis.database=0
spring.data.redis.password=
spring.data.redis.port=6373
spring.data.redis.host=127.0.0.1
spring.data.redis.lettuce.pool.min-idle=5
spring.data.redis.lettuce.pool.max-idle=10
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-wait=1ms
spring.data.redis.lettuce.shutdown-timeout=100ms

# mail
spring.mail.host=smtp.163.com
spring.mail.username=
spring.mail.password=
spring.mail.properties.mail.smtl.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true

# 生产环境中删除下面
mybatis.configuration.log-impl=org.apache.ibatis.logging.stdout.StdOutImpl
mybatis.configuration.jdbc-type-for-null=NULL


```
### jwt用密钥对
生成工程必要的密钥对，推荐使用openssl工具生成，参考如下
1. 私钥: `openssl genrsa -out private.pem`
2. 公钥 `openssl rsa -in private.pem -inform pem -pubout -out public.pem`
