package com.example.common.autoconfig;


import com.aliyun.dypnsapi20170525.Client;
import com.aliyun.teaopenapi.models.Config;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

@Slf4j
@Data
@AutoConfiguration
@ConfigurationProperties(prefix = "example.aliyun")
public class AliyunConfig {

    private String accessKeyId;
    private String accessKeySecret;


    /**
     * 初始化 alibaba 的 号码认证服务 的 客户端
     * (可参考 <a href="https://help.aliyun.com/zh/sdk/developer-reference/v2-manage-access-credentials?spm=a2c4g.11186623.help-menu-262060.d_1_4_1_3.2b5c86e9tF0QCt">阿里云SDK</a>)
     */
    @Bean("phoneAuthClient")
    public Client phoneAuthClient() throws Exception {
        log.info("初始化 alibaba 的 号码认证服务 的 客户端");
        // 配置凭据
        com.aliyun.credentials.models.Config credentialConfig = new com.aliyun.credentials.models.Config();
        credentialConfig.setType("access_key");
        credentialConfig.setAccessKeyId(accessKeyId);
        credentialConfig.setAccessKeySecret(accessKeySecret);
        // 得到 凭据客户端
        com.aliyun.credentials.Client credentialClient = new com.aliyun.credentials.Client(credentialConfig);
        // 使用凭据得到客户端配置
        Config config = new Config()
                .setCredential(credentialClient)
                .setEndpoint("dypnsapi.aliyuncs.com");
        // 初始化客户端
        return new Client(config);
    }


}
