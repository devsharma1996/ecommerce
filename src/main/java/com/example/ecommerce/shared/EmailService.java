package com.example.ecommerce.shared;

import com.example.ecommerce.security.SecurityConstants;
import com.example.ecommerce.shared.dto.UserDto;


import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


@Service
public class EmailService {



    final String FROM = "sergey.kargopolov@swiftdeveloperblog.com";

    // The subject line for the email.
    final String SUBJECT = "One last step to complete your registration with PhotoApp";

    final String PASSWORD_RESET_SUBJECT = "Password reset request";

    // The HTML body for the email.
    final String HTMLBODY = "<h1>Please verify your email address</h1>"
            + "<p>Thank you for registering with our mobile app. To complete registration process and be able to log in,"
            + " click on the following link: "
            + "<a href='http://localhost:8080/verification-service/email-verification.html?token=$tokenValue'>"
            + "Final step to complete your registration" + "</a><br/><br/>"
            + "Thank you! And we are waiting for you inside!";


    final String PASSWORD_RESET_HTMLBODY = "<h1>A request to reset your password</h1>"
            + "<p>Hi, $firstName!</p> "
            + "<p>Someone has requested to reset your password with our project. If it were not you, please ignore it."
            + " otherwise please click on the link below to set a new password: "
            + "<a href='http://localhost:8080/verification-service/password-reset.html?token=$tokenValue'>"
            + " Click this link to Reset Password"
            + "</a><br/><br/>"
            + "Thank you!";



    public void verifyEmail(UserDto userDto) {


        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host",SecurityConstants.getSmtpHost());
        properties.put("mail.smtp.port",SecurityConstants.getSmtpPort());
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SecurityConstants.getSmtpUsername(),SecurityConstants.getSmtpPassword());
                    }
                });


        MimeMessage mimeMessage=new MimeMessage(session);
        String htmlBodyWithToken = HTMLBODY.replace("$tokenValue", userDto.getEmailVerificationToken());

        try{

            mimeMessage.setFrom(new InternetAddress(FROM));
            mimeMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(userDto.getEmail()));

            mimeMessage.setSubject(SUBJECT);
            mimeMessage.setContent(htmlBodyWithToken,"text/html" );

            Transport.send(mimeMessage);
            System.out.println("message sent....");
        }catch (MessagingException ex ){
            throw  new RuntimeException(ex.getMessage());
        }
    }

    public boolean sendPasswordResetEmail(String firstName,String email,String token){

        boolean returnValue = true;


        String htmlBodyWithToken = PASSWORD_RESET_HTMLBODY.replace("$tokenValue", token);
        htmlBodyWithToken = htmlBodyWithToken.replace("$firstName", firstName);

        Properties properties = System.getProperties();
        properties.put("mail.transport.protocol", "smtp");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host",SecurityConstants.getSmtpHost());
        properties.put("mail.smtp.port",SecurityConstants.getSmtpPort());
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SecurityConstants.getSmtpUsername(),SecurityConstants.getSmtpPassword());
                    }
                });


        MimeMessage mimeMessage=new MimeMessage(session);


        try{

            mimeMessage.setFrom(new InternetAddress(FROM));
            mimeMessage.addRecipient(Message.RecipientType.TO,
                    new InternetAddress(email));

            mimeMessage.setSubject(PASSWORD_RESET_SUBJECT);
            mimeMessage.setContent(htmlBodyWithToken,"text/html" );

            Transport.send(mimeMessage);
            System.out.println("message sent....");
        }catch (MessagingException ex ){
            returnValue=false;
        }finally {
            return returnValue;
        }

    }


}
