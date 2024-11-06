# 销售猫- WMS项目

## 部署步骤
# JWT密钥对(*必要*)

生成工程必要的密钥队，推荐使用openssl工具生成，参考如下
1. 私钥: `openssl genrsa -out private.pem`
2. 公钥 `openssl rsa -in privkey.pem -inform pem -pubout -out public.pem`

## 其他注意事项
* Jason会将Date转化为+8时区的日期，所以存入时请先将日期转为+8时区的日期，防止出现时区差（前端获取到的也是+8时区时间，请根据本地需求自行转换）