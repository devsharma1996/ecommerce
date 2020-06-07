package com.example.ecommerce.shared;

import com.example.ecommerce.security.SecurityConstants;
import com.example.ecommerce.shared.dto.UserDto;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class EmailSender {



    public void verifyEmail(UserDto userDto) {


    }


}
