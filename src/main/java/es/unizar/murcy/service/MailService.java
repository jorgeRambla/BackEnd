package es.unizar.murcy.service;

import org.aspectj.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;

@Service
public class MailService {

    @Autowired
    public JavaMailSender emailSender;

    @Value("${spring.mail.enable}")
    private Boolean mailEnabled;

    @Value("${murcy.back-end.application-url}")
    private String currentBackEndURL;

    @Value("${murcy.front-end.application-url}")
    private String currentFrontEndURL;

    Logger logger = LoggerFactory.getLogger(MailService.class);

    public void sendTokenConfirmationMail(String token, String email) {

        try {
            File file = ResourceUtils.getFile("classpath:templates/confirmation-template.vm");
            String template = FileUtil.readAsString(file);
            final String finalTemplate = formatTemplate(template, token);
            if(mailEnabled.equals(Boolean.TRUE)) {
                MimeMessagePreparator preparation = mimeMessage -> {
                    MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
                    message.setTo(email);
                    message.setFrom("no-reply@murcy.es");
                    message.setText(finalTemplate, true);
                };
                emailSender.send(preparation);
            } else {
                logger.info("[FAKE EMAIL] From: " + email + ", body: " + token);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String formatTemplate(String template, String token) {
        template = template.replace("${blank_gmail}", currentBackEndURL.concat("/blank.gif"));
        template = template.replace("${logotipo_URL}", currentBackEndURL.concat("/logotype.jpeg"));

        // TODO: Change to FrontEndURL
        template = template.replace("${confirmation_URL}", currentBackEndURL.concat("/api/user/confirm/").concat(token));
        template = template.replace(("${MURCY_URL}"), currentFrontEndURL);

        return template;
    }

}
