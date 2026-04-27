package com.example.common.properties;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 阿里云配置属性类
 */
@Data
@ConfigurationProperties(prefix = "example.aliyun")
public class AliYunProperties {

    /// 阿里云访问密钥ID
    private String accessKeyId = "";
    /// 阿里云访问密钥密钥
    private String accessKeySecret = "";
}
