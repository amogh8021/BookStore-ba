package com.example.bookStore.demo.Service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class EmailService {



    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private  String fromEmail;


    public void SendOtpOnSignup(String UserEmail, String otp ){


        //logic to send email to user on signup for verification





        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(UserEmail);
        message.setSubject("otp verification");
        message.setText("welcome to bookstore , you can shop the variety of book here.\n  your otp for signup is " + otp );
        javaMailSender.send(message);


    }

    public void sendForgotPassword(String email, String otp){
        SimpleMailMessage message = new SimpleMailMessage();

        message.setTo(email);
        message.setText("your otp for the password reset is " + otp);
        message.setSubject("reset-otp");
        message.setFrom(fromEmail);

        javaMailSender.send(message);
    }



}
