package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.dto.UserDto;
import es.unizar.murcy.model.request.RegisterUserRequest;
import es.unizar.murcy.model.request.UpdateUserRequest;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.MailService;
import es.unizar.murcy.service.MailServiceRule;
import es.unizar.murcy.service.UserService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.UUID;

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

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Rule
    public MailServiceRule mailServiceRule = new MailServiceRule();

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String randomToken;
    private User userUser;
    private String userUserToken;
    private User editorUser;
    private String editorUserToken;
    private User reviewerUser;
    private String reviewerUserToken;

    @Before
    public void setUp()  {
        User user = new User("testUser", new BCryptPasswordEncoder().encode("test"), "testUser@test.com", "Test Test");
        user.setConfirmed(true);
        this.userUser = userService.create(user);

        user = new User("testEditor", new BCryptPasswordEncoder().encode("test"), "testEditor@test.com", "Test Test");
        user.setConfirmed(true);
        user.addRol(User.Rol.EDITOR);
        this.editorUser = userService.create(user);

        user = new User("testReviewer", new BCryptPasswordEncoder().encode("test"), "testReviewer@test.com", "Test Test");
        user.setConfirmed(true);
        user.addRol(User.Rol.REVIEWER);
        this.reviewerUser = userService.create(user);

        this.userUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(userUser.getUsername()));
        this.editorUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(editorUser.getUsername()));
        this.reviewerUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(reviewerUser.getUsername()));
        this.randomToken = userUserToken.substring(0, userUserToken.length()-3).concat("aaa");
    }

    @Test
    public void test_POST_api_user_201() throws JsonProcessingException {

        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("newTest");
        registerUserRequest.setEmail("newTest@test.com");
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_1() throws JsonProcessingException {

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
    public void test_POST_api_user_400_2() throws JsonProcessingException {

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
    public void test_POST_api_user_400_3() throws JsonProcessingException {

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
    public void test_POST_api_user_400_4() throws JsonProcessingException {

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
    public void test_POST_api_user_400_5() throws JsonProcessingException {


        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("testUser");
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
    public void test_POST_api_user_400_6() throws JsonProcessingException {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setUsername("test");
        registerUserRequest.setEmail("testUser@test.com");
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
    public void test_GET_api_user_info_500_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_200_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(userUser.getId(), userDto.getId());
        assertEquals(userUser.getUsername(), userDto.getUserName());
        assertEquals(userUser.getEmail(), userDto.getEmail());
    }

    @Test
    public void test_GET_api_user_info_200_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
    }

    @Test
    public void test_GET_api_user_info_200_3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
    }

    @Test
    public void test_GET_api_user_info_ID_500_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_ID_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_ID_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_ID_401_3() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_ID_404_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_ID_200_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);

        assertEquals(editorUser.getId(), userDto.getId());
    }

    @Test
    public void test_GET_api_user_info_ID_200_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);

        assertEquals(editorUser.getId(), userDto.getId());
    }

    @Test
    public void test_PUT_api_user_info_ID_500_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_404_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newMail@mail.com", "Test test", "testTest", new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_401_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newMail@mail.com", "Test test", "testTest", new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_400_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(editorUser.getEmail(), "Test test", "testTest", new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + userUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_400_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newTest@test.com", "Test test", editorUser.getUsername(), new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + userUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}