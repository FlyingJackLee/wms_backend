#!/bin/bash

# 输出0表示成功，1表示系统变量设置脚本不存在, 2表示shell配置文件错误, 3表示系统变量设置失败
project_path="/www/wwwroot/api.flyingjack.top"

# 检查目录是否存在
if [ ! -d "$project_path" ]; then
  # 项目目录不存在, 创建
  mkdir -p "project_path"
fi

# 检查JWT密钥对
if [ ! -f "$project_path/private.pem" ] || [ ! -f "$project_path/public.pem" ]; then
  # 不存在秘钥对，开始创建
  # 删除已存在的private.pem或 public.pem
  if [ -f "$project_path/private.pem" ]; then
    rm "$project_path/private.pem"
  fi
  if [ -f "$project_path/public.pem" ]; then
    rm "$project_path/public.pem"
  fi

  openssl genrsa -out "$project_path/private.pem"
  openssl rsa -in "$project_path/private.pem" -inform pem -pubout -out "$project_path/public.pem"
fi

# "**开始检查系统变量设置**"
# 定义一个函数来批量检查系统变量是否存在
check_variables() {
  local variables=("$@")
  for var in "${variables[@]}"; do
    if [ -z "${!var}" ]; then
      echo "$var"
      return 1  # 返回1表示false
    fi
  done
  return 0  # 所有变量都存在时返回0表示true
}

check_variables "WMS_PORT" "WMS_REDIS_HOST" "WMS_REDIS_PORT" "WMS_ALIYUN_ACCESS_ID" "WMS_ALIYUN_ACCESS_SECRET" "WMS_ALIYUN_ACCESS_SIGN_NAME" "WMS_ALIYUN_ACCESS_TEMPLATE_CODE" "WMS_MAIL_HOST" "WMS_MAIL_USERNAME" "WMS_MAIL_PASSWORD" "WMS_DATASOURCE_URL" "WMS_DATASOURCE_USERNAME" "WMS_DATASOURCE_PASSWORD" "JWT_PRIVATE_KEY_PATH" "JWT_PUBLIC_KEY_PATH"
if [ $? -eq 1 ]; then
  #有系统变量检查不通过，开始检查系统变量设置脚本
  if [ ! -f "$project_path/wms_envs.sh" ]; then
      #系统环境设置标本$project_path/wms_envs.sh不存在, 终止
      echo 1
      exit 1
  else
    cp $project_path/wms_envs.sh "/etc/profile.d/wms_envs.sh"
    # 设置脚本权限
    chmod +x "/etc/profile.d/wms_envs.sh"
    source /etc/profile
  fi
fi
