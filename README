# 销售猫- WMS项目

## 流水线部署
请参考项目目录下的`Jenkinsfile`，该脚本运行前请确保Jenkins已配置了目标机的agent（label为prod的agent），且目标机上安装了jdk21，其余步骤请根据需要更改


## 手动部署步骤

###  第一步 确认JWT密钥对已经生成并设置系统环境
生成工程必要的密钥队，推荐使用openssl工具生成，参考如下
1. 私钥: `openssl genrsa -out private.pem`
2. 公钥 `openssl rsa -in privkey.pem -inform pem -pubout -out public.pem`
3. 并将上述公钥地址设置进系统环境 `JWT_PRIVATE_KEY_PATH` `JWT_PUBLIC_KEY_PATH`

### 第二步 确认必要系统环境已经设置
    # 阿里云短信接口
    WMS_ALIYUN_ACCESS_ID
    WMS_ALIYUN_ACCESS_SECRET
    WMS_ALIYUN_ACCESS_SIGN_NAME
    WMS_ALIYUN_ACCESS_TEMPLATE_CODE
    
    # 邮件服务接口
    WMS_MAIL_HOST
    WMS_MAIL_USERNAME
    WMS_MAIL_PASSWORD
   
    # Redis设置
    WMS_REDIS_PORT
    WMS_REDIS_HOST

    # 其他
    WMS_PORT 端口

### 第三步 打包项目
可以参考`deploy/pre/Jenkinsfile` 流水线文件进行项目打包

### 第四步 运行jar包（参考）

`/www/server/java/jdk-21.0.2/bin/java -Dcom.sun.management.jmxremote.port=6488 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.hostname=127.0.0.1 -jar -Xmx1024M -Xms256M  /www/wwwroot/api.flyingjack.top/wms_backend_beta.jar --server.port=8080`

### 第五步 配置nginx反向代理

如果使用宝塔的话(假设你已经设置好java项目)，可以直接参考以下步骤：
1. 停止java服务: 手动停止已经在运行的包或者使用命令 `pkill -f 'java.+wms'`
2. 替换项目包 
3. 启动java服务


## 其他注意事项
* Jason会将Date转化为+8时区的日期，所以存入时请先将日期转为+8时区的日期，防止出现时区差（前端获取到的也是+8时区时间，请根据本地需求自行转换）