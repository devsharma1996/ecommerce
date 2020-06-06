package com.example.ecommerce.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppProperties {

    @Autowired
    private Environment env;

    public String getTokenSecret(){
        return env.getProperty("tokenSecret");
    }

    public String getSmtpHost(){
        return env.getProperty("spring.mail.host");
    }

    public String getSmtpPort(){
        return env.getProperty("spring.mail.port");
    }

    public String getSmtpUsername(){
        return env.getProperty("spring.mail.username");
    }

    public String getSmtpPassword(){
        return env.getProperty("spring.mail.password");
    }
}