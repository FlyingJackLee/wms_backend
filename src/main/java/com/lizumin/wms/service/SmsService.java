package com.lizumin.wms.service;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {
    private static final Logger logger = LoggerFactory.getLogger(MailService.class);

    @Value("${aliyun.access.id}")
    private String accessId;

    @Value("${aliyun.access.secret}")
    private String accessSecret;

    /**
     * 使用AK&SK初始化账号Client
     * @return Client
     * @throws Exception
     */
    public Client createClient() throws Exception {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
                .setAccessKeyId(accessId)
                .setAccessKeySecret(accessSecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 发送验证码
     *
     * @param phoneNumber 发送对象手机号码
     * @param templateParam 模板参数，需要json化
     * @param signName 签名名称
     * @param templateCode 模板code
     */
    public boolean sendSms(String phoneNumber, String templateParam, String signName, String templateCode) throws Exception {
        Client client = this.createClient();
        SendSmsRequest sendSmsRequest = new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setPhoneNumbers(phoneNumber)
                .setTemplateParam(templateParam);

        RuntimeOptions runtime = new RuntimeOptions();

        boolean isSuccess = true;

        try {
            SendSmsResponse response = client.sendSmsWithOptions(sendSmsRequest, runtime);
        } catch (TeaException error) {
            SmsService.logger.error(error.message);
            isSuccess = false;
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            SmsService.logger.error(error.message);
            isSuccess = false;
        }

        return isSuccess;
    }
}
