package com.eyangless.Back.Service;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void envoieMailhtml(String to, String subject, String htmlbody) throws MessagingException;
    void sendOtpEmail(String email, String otp);

}
