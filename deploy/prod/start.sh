#!/bin/bash

JAR_PATH=$1

# 使用Shell命令检查文件是否存在，并在存在时备份
if [ -f "$JAR_PATH/wms_backend.jar" ]; then
    BACKUP_FILE="$JAR_PATH/wms_backend_$(date +%Y%m%d%H%M%S).jar"
    cp "$JAR_PATH/wms_backend.jar" "$BACKUP_FILE"
fi


# 备份日志
if [ -f /home/www/log/wms_backend.log ]; then
    LOG_BACKUP_FILE="/home/www/log/wms_backend_$(date +%Y%m%d%H%M%S).log"
    cp /home/www/log/wms_backend.log $LOG_BACKUP_FILE
    rm /home/www/log/wms_backend.log
fi

# 运行项目
sudo -u www bash -c  "source /etc/profile.d/wms_envs.sh && nohup java -jar $JAR_PATH/wms_backend.jar > /home/www/log/wms_backend.log 2>&1 &"

# 检查包含 "wms" 的进程是否存在
# 初始化计数器
counter=0
process_found=1

while [ $counter -lt 5 ]
do
    if ps aux | grep 'wms' > /dev/null
    then
        process_found=0
        break
    fi
    # 等待1秒
    sleep 1
    # 增加计数器
    counter=$((counter + 1))
done

if [ $process_found -eq 0 ]; then
  echo 0
else
  echo 1
  exit 1
fi