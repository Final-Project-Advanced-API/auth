package org.example.userservice.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendMail(String email,String otpCode) throws MessagingException;
}
