package com.example.ecommerce.security;

import com.example.ecommerce.SpringApplicationContext;

public class SecurityConstants {

    public static final long EXPIRATION_TIME=864000;
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING="Authorization";
    public static final String SIGN_UP_URL="/users";
    public static final String VERIFICATION_EMAIL_URL="/users/email-verification";

    public static String getTokenSecret(){
        AppProperties appProperties=(AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getTokenSecret();
    }

    public static String getSmtpHost(){
        AppProperties appProperties=(AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getSmtpHost();
    }

    public static String getSmtpPort(){
        AppProperties appProperties=(AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getSmtpPort();
    }

    public static String getSmtpUsername(){
        AppProperties appProperties=(AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getSmtpUsername();
    }

    public static String getSmtpPassword(){
        AppProperties appProperties=(AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getSmtpPassword();
    }

}
