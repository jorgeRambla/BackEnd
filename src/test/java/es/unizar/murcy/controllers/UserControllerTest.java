package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.Token;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.UserDto;
import es.unizar.murcy.model.request.JsonWebTokenRequest;
import es.unizar.murcy.model.request.RegisterUserRequest;
import es.unizar.murcy.model.request.UpdateUserRequest;
import es.unizar.murcy.service.*;
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
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static es.unizar.murcy.model.Token.DEFAULT_TOKEN_EXPIRATION_TIME;
import static org.junit.Assert.*;

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

    @Autowired
    private TokenService tokenService;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String randomToken;
    private User userUser;
    private String userUserToken;
    private User editorUser;
    private String editorUserToken;
    private User reviewerUser;
    private String reviewerUserToken;
    private User adminUser;
    private String adminUserToken;
    private User unconfirmedUser;
    private Token unconfirmedUserToken;

    @Before
    public void setUp()  {
        User user = new User("testUser", new BCryptPasswordEncoder().encode("test"), "testUser@test.com", "Test Test");
        user.setConfirmed(true);
        user.addRol(User.Rol.USER);
        this.userUser = userService.create(user);

        user = new User("testEditor", new BCryptPasswordEncoder().encode("test"), "testEditor@test.com", "Test Test");
        user.setConfirmed(true);
        user.addRol(User.Rol.EDITOR);
        this.editorUser = userService.create(user);

        user = new User("testReviewer", new BCryptPasswordEncoder().encode("test"), "testReviewer@test.com", "Test Test");
        user.setConfirmed(true);
        user.addRol(User.Rol.REVIEWER);
        this.reviewerUser = userService.create(user);

        user = new User("testAdmin", new BCryptPasswordEncoder().encode("test"), "testAdmin@test.com", "Test Test");
        user.setConfirmed(true);
        user.addRol(User.Rol.ADMINISTRATOR);
        this.adminUser = userService.create(user);

        user = new User("unconfirmedUser", new BCryptPasswordEncoder().encode("test"), "unconfirmedUser@test.com", "Test Test");
        this.unconfirmedUser = userService.create(user);
        this.unconfirmedUserToken = tokenService.create(new Token(unconfirmedUser, UUID.randomUUID().toString(), new Date(System.currentTimeMillis() + DEFAULT_TOKEN_EXPIRATION_TIME)));

        this.userUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(userUser.getUsername()));
        this.editorUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(editorUser.getUsername()));
        this.reviewerUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(reviewerUser.getUsername()));
        this.adminUserToken = jsonWebTokenUtil.generateToken(userDetailsService.loadUserByUsername(adminUser.getUsername()));
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
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");
        registerUserRequest.setUsername("newUser");
        registerUserRequest.setEmail("testUser@test.com");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_400_7() throws JsonProcessingException {
        RegisterUserRequest registerUserRequest = new RegisterUserRequest();
        registerUserRequest.setFullName("Test test");
        registerUserRequest.setPassword("test");
        registerUserRequest.setUsername("newUser");
        registerUserRequest.setEmail("novalidmail");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(registerUserRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/user", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_api_user_info_401_2() {
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
    public void test_GET_api_user_info_ID_401_4() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
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
        headers.setBearerAuth(reviewerUserToken);

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
    public void test_PUT_api_user_info_ID_404_1() throws Exception {
         HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

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
    public void test_PUT_api_user_info_ID_401_3() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);

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

    @Test
    public void test_PUT_api_user_info_ID_400_3() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newTest@test.com", "Test test", editorUser.getUsername(), new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + userUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_400_4() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("novalidmail", "Test test", editorUser.getUsername(), new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + userUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_3() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null, null, null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_4() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", new String[]{});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_5() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", new String[]{User.Rol.REVIEWER.name()});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_6() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", new String[]{User.Rol.REVIEWER.name()});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(1, userDto.getRole().length);
        assertEquals(User.Rol.REVIEWER, User.Rol.valueOf(userDto.getRole()[0]));

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_7() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newValid@mail.com", "", "", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals("newValid@mail.com", userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_8() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "new", "", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals("new", userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_9() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "new", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals("new", userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_10() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "new", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertNotEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_11() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(editorUser.getEmail(), "", "", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_ID_201_12() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", editorUser.getUsername(), "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + editorUser.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info/" + -1), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_400_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(editorUser.getEmail(), "Test test", "testTest", new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_400_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newTest@test.com", "Test test", editorUser.getUsername(), new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_400_3() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newTest@test.com", "Test test", editorUser.getUsername(), new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_400_4() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("novalidmail", "Test test", editorUser.getUsername(), new BCryptPasswordEncoder().encode("newPass"), new String[]{"USER", "EDITOR"});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_api_user_info_201_3() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(null, null, null, null, null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_4() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", new String[]{});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_5() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", new String[]{User.Rol.REVIEWER.name()});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(editorUser.getId(), userDto.getId());
        assertEquals(editorUser.getUsername(), userDto.getUserName());
        assertEquals(editorUser.getFullName(), userDto.getFullName());
        assertEquals(editorUser.getEmail(), userDto.getEmail());
        assertEquals(editorUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(editorUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(editorUser.getId());

        assertTrue(user.isPresent());

        assertEquals(editorUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_6() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "", new String[]{User.Rol.REVIEWER.name()});

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals(reviewerUser.getFullName(), userDto.getFullName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
        assertEquals(1, userDto.getRole().length);
        assertEquals(User.Rol.REVIEWER, User.Rol.valueOf(userDto.getRole()[0]));

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_7() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("newValid@mail.com", "", "", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals(reviewerUser.getFullName(), userDto.getFullName());
        assertEquals("newValid@mail.com", userDto.getEmail());
        assertEquals(reviewerUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(reviewerUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_8() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "new", "", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals("new", userDto.getFullName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
        assertEquals(reviewerUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(reviewerUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_9() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "new", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals("new", userDto.getUserName());
        assertEquals(reviewerUser.getFullName(), userDto.getFullName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
        assertEquals(reviewerUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(reviewerUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_10() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", "", "new", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals(reviewerUser.getFullName(), userDto.getFullName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
        assertEquals(reviewerUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(reviewerUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertNotEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_11() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest(reviewerUser.getEmail(), "", "", "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals(reviewerUser.getFullName(), userDto.getFullName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
        assertEquals(reviewerUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(reviewerUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_PUT_api_user_info_201_12() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateUserRequest updateUserRequest = new UpdateUserRequest("", "", reviewerUser.getUsername(), "", null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/info"), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateUserRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        UserDto userDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), UserDto.class);
        assertEquals(reviewerUser.getId(), userDto.getId());
        assertEquals(reviewerUser.getUsername(), userDto.getUserName());
        assertEquals(reviewerUser.getFullName(), userDto.getFullName());
        assertEquals(reviewerUser.getEmail(), userDto.getEmail());
        assertEquals(reviewerUser.getRoles().size(), userDto.getRole().length);
        for(String rol : userDto.getRole()) {
            assertTrue(reviewerUser.getRoles().contains(User.Rol.valueOf(rol)));
        }

        Optional<User> user = userService.findUserById(reviewerUser.getId());

        assertTrue(user.isPresent());

        assertEquals(reviewerUser.getPassword(), user.get().getPassword());
    }

    @Test
    public void test_POST_api_user_confirm_TOKEN_201_1(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/confirm/" + unconfirmedUserToken.getTokenValue()), HttpMethod.POST, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_confirm_TOKEN_404_1(){
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/confirm/" + "NotFoundToken"), HttpMethod.POST, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_login_403_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonWebTokenRequest jsonWebTokenRequest = new JsonWebTokenRequest("notExistsUser", "pass");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/login"), HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(jsonWebTokenRequest), headers), Object.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_login_403_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonWebTokenRequest jsonWebTokenRequest = new JsonWebTokenRequest(unconfirmedUser.getUsername(), "pass");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/login"), HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(jsonWebTokenRequest), headers), Object.class);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_login_401_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonWebTokenRequest jsonWebTokenRequest = new JsonWebTokenRequest(userUser.getUsername(), "wrongPassword");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/login"), HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(jsonWebTokenRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_api_user_login_200_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        JsonWebTokenRequest jsonWebTokenRequest = new JsonWebTokenRequest(userUser.getUsername(), "test");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/user/login"), HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(jsonWebTokenRequest), headers), Object.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}