package es.unizar.murcy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class MailService {

    @Value("${spring.mail.enable}")
    private Boolean mailEnabled;

    @Value("${murcy.config.back-end.application-url}")
    private String currentBackEndURL;

    @Value("${murcy.config.front-end.application-url}")
    private String currentFrontEndURL;

    private final JavaMailSender emailSender;
    private final ResourceLoader resourceLoader;

    private Logger logger = LoggerFactory.getLogger(MailService.class);

    public MailService(JavaMailSender emailSender, ResourceLoader resourceLoader) {
        this.emailSender = emailSender;
        this.resourceLoader = resourceLoader;
    }

    public void sendTokenConfirmationMail(String token, String email) {

        try {
            InputStream  inputStream = resourceLoader.getResource("classpath:templates/confirmation-template.vm").getInputStream();
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String template =  result.toString(StandardCharsets.UTF_8.name());
            final String finalTemplate = formatTemplate(template, token);
            if(mailEnabled.equals(Boolean.TRUE)) {
                MimeMessagePreparator preparation = mimeMessage -> {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(email);
                    message.setSubject("Confirm account");
                    message.setText(finalTemplate, true);
                };
                logger.debug("[FAKE EMAIL] From: {}, body: {}", email, token);
                emailSender.send(preparation);
            } else {
                logger.info("[FAKE EMAIL] From: {}, body: {}", email, token);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String formatTemplate(String template, String token) {
        template = template.replace("${blank_gmail}", currentBackEndURL.concat("/blank.gif"));
        template = template.replace("${logotipo_URL}", currentBackEndURL.concat("/logotype.jpeg"));
        template = template.replace("${confirmation_URL}", currentFrontEndURL.concat("/confirm-token/").concat(token));
        template = template.replace(("${MURCY_URL}"), currentFrontEndURL);
        return template;
    }

}
