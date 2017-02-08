package com.digitalchina.sport.order.api.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class Config {
    @Value("${sport.order.pagesize}")
    public String pageSize;

}
