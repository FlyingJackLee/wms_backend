# 销售猫- WMS项目

## 部署步骤


部署使用Jenkins流水线，需要注意
1. 确认在agent可以连接到仓库（Github国内服务器无法访问),  
   注意：使用阿里云托管的话，需要配置白名单和密钥，以保证agent可以连接到仓库。
2. 环境部署时，请将agent命名为
   1. beta: beta
   2. prod: prod
3. 配置文件存于独立的codeup仓库，请注意保密

# JWT密钥对(*必要*)

生成工程必要的密钥队，推荐使用openssl工具生成，参考如下
1. 私钥: `openssl genrsa -out private.pem`
2. 公钥 `openssl rsa -in privkey.pem -inform pem -pubout -out public.pem`

## 其他注意事项
* Jason会将Date转化为+8时区的日期，所以存入时请先将日期转为+8时区的日期，防止出现时区差（前端获取到的也是+8时区时间，请根据本地需求自行转换）