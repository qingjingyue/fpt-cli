package com.example.common.autoconfig;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * 配置类，用于加载属性配置
 */
@AutoConfiguration
@ConfigurationPropertiesScan(basePackages = "com.example.common.properties")
public class PropertiesConfig {
}
