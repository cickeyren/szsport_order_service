package com.digitalchina;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BootApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(BootApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(BootApplication.class, args);
    }
    //
    //@Bean
    //public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
    //    ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
    //    registration.getUrlMappings().clear();
    //    registration.addUrlMappings("*.do");
    //    return registration;
    //}
}
