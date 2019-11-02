package es.unizar.murcy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    public JavaMailSender emailSender;

    @Value("${spring.mail.enable}")
    private Boolean mailEnabled;

    Logger logger = LoggerFactory.getLogger(MailService.class);


    public void sendTokenConfirmationMail(String token, String email) {
        if(mailEnabled.equals(Boolean.TRUE)) {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(email);
            simpleMailMessage.setSubject("Confirmacion de registro");
            simpleMailMessage.setText("Confirma token: " + token);
            emailSender.send(simpleMailMessage);
        } else {
            logger.info("[FAKE EMAIL] From: " + email + ", body: " + token);
        }
    }

}
