package es.unizar.murcy.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    public JavaMailSender emailSender;

    public void sendTokenConfirmationMail(String token, String email) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Confirmacion de registro");
        simpleMailMessage.setText("Confirma token: " + token);
        emailSender.send(simpleMailMessage);
    }

}
