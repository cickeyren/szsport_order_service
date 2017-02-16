package com.digitalchina.sport.order.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(locations = "classpath:application.properties",prefix="application")
public class Config {
    @Value("${sport.order.pagesize}")
    public String pageSize;

}
