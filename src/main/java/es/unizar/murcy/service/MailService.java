package es.unizar.murcy.service;

import es.unizar.murcy.client.MailClient;
import es.unizar.murcy.client.MailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MailService {

    private final MailClient mailClient;

    @Value("${murcy.config.back-end.application-url}")
    private String currentBackEndURL;

    @Value("${murcy.config.front-end.application-url}")
    private String currentFrontEndURL;

    public MailService(MailClient mailClient) {
        this.mailClient = mailClient;
    }

    public void sendConfirmationTokenMail(String token, String toEmail, String username) throws Exception {
        MailRequest mailRequest = new MailRequest();
        mailRequest.setToEmail(toEmail);
        mailRequest.setTemplateName("SIGN_UP_CONFIRM_TOKEN");
        mailRequest.setArguments(Stream.of(
                    new AbstractMap.SimpleImmutableEntry<>("BACK_END_URL", currentBackEndURL),
                    new AbstractMap.SimpleImmutableEntry<>("USER", username),
                    new AbstractMap.SimpleImmutableEntry<>("CONFIRMATION_URL", currentFrontEndURL.concat("?token=").concat(token)),
                    new AbstractMap.SimpleImmutableEntry<>("MURCY_URL", currentFrontEndURL))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        try {
            mailClient.sendMail(mailRequest);
        } catch (Exception e) {
            throw e;
        }
    }
}
