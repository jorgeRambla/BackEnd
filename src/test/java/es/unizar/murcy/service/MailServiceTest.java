package es.unizar.murcy.service;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.internet.MimeMessage;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MailServiceTest {

    @Rule
    public MailServiceRule mailServiceRule = new MailServiceRule();

    @Autowired
    MailService mailService;

    @Test
    public void sendMail() {
        String token = UUID.randomUUID().toString();
        String mail = "test@test.com";
        mailService.sendTokenConfirmationMail(token, mail);
        MimeMessage[] receivedMessages = mailServiceRule.getMessages();

        assertEquals(1, receivedMessages.length);
    }
}
