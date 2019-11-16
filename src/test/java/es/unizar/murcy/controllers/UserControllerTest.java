package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.model.request.RegisterUserRequest;
import es.unizar.murcy.service.MailService;
import es.unizar.murcy.service.SmtpServerRule;
import es.unizar.murcy.service.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @LocalServerPort
    private int port;

    @MockBean
    private MailService mailService;

    @Autowired
    private UserService userService;

    @Rule
    public SmtpServerRule smtpServerRule = new SmtpServerRule(2525);

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Test
    public void test_POST_api_user_201() throws JsonProcessingException {

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setEmail("test@test.com");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_incomplete_1() throws JsonProcessingException {

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setEmail("test@test.com");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_incomplete_2() throws JsonProcessingException {

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_incomplete_3() throws JsonProcessingException {

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setEmail("test@test.com");
        registerUserRequest.setPassword("test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_incomplete_4() throws JsonProcessingException {

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setEmail("test@test.com");
        registerUserRequest.setFullName("Test test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_duplicated_username() throws JsonProcessingException {


        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test2");
        registerUserRequest.setEmail("test2@test.com");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        userService.create(registerUserRequest.toEntity());

        registerUserRequest.setUsername("test2");
        registerUserRequest.setEmail("test@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_duplicated_mail() throws JsonProcessingException {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test2");
        registerUserRequest.setEmail("test2@test.com");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        userService.create(registerUserRequest.toEntity());

        registerUserRequest.setUsername("test");
        registerUserRequest.setEmail("test2@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info() {
        ResponseEntity response = restTemplate.getForEntity("http://localhost:" + port + "/api/user/info", Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}