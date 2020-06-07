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

    public String getSendGridApiKey(){
        return env.getProperty("sendGridApiKey");
    }


}