package es.unizar.murcy.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.Duration;

@Service
public class MailClient {

    private final RestTemplate restTemplate;

    private final Logger logger = LoggerFactory.getLogger(MailClient.class);

    @Value("${murcy.mail.uri}")
    private String uri;

    @Value("${murcy.mail.api-key}")
    private String apiKey;

    public MailClient() {
        this.restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMinutes(15))
                .setReadTimeout(Duration.ofMinutes(15)).build();
    }

    private URI uriBuilder() {
        return UriComponentsBuilder.fromUriString(uri).path("/mail/send").build().toUri();
    }

    public void sendMail(MailRequest mailRequest) {
        try {
            HttpHeaders headers = new HttpHeaders();

            headers.add("murcy-api-key", apiKey);

            HttpEntity<MailRequest> request = new HttpEntity<>(mailRequest, headers);

            restTemplate.postForEntity(uriBuilder(), request, String.class);
        } catch (HttpStatusCodeException hsce) {
            logger.error("{} - {} from mail client", hsce.getStatusCode(), hsce.getResponseBodyAsString());
            throw hsce;
        }
    }
}
