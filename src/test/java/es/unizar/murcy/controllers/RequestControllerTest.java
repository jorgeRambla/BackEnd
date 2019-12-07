package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.net.URI;
import java.util.Set;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    UserService userService;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtUserDetailsService userDetailsService;

    private String randomToken;
    private String userUserToken;
    private User editorUser;
    private String editorUserToken;
    private User reviewerUser;
    private String reviewerUserToken;

    @Before
    public void setUp()  {
        User user = new User("testUser", new BCryptPasswordEncoder().encode("test"), "testUser@test.com", "Test Test");
        user.setConfirmed(true);
        User userUser = userService.create(user);

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
    public void test_GET_current_user_editor_request_404_1() throws Exception {
        test_CREATE_current_user_editor_request_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/request/editor/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }


    @Test
    public void test_GET_current_user_editor_request_401_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.GET, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }



    @Test
    public void test_GET_current_user_editor_request_401_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.GET, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    @Test
    public void test_PUT_current_user_editor_request_404_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/request/editor/" + -1), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }


    @Test
    public void test_PUT_current_user_editor_request_401_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_current_user_editor_request_401_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_current_user_editor_request_201_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }


    @Test
    public void test_CREATE_current_user_editor_request_401_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    public void test_CREATE_current_user_editor_request_401_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    public void test_CREATE_current_user_editor_request_201_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/"),
                HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    public void test_GET_OPENED_user_editor_request_401_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/list"),
                HttpMethod.GET, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    public void test_GET_OPENED_user_editor_request_401_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/list"),
                HttpMethod.GET, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

    }

    @Test
    public void test_GET_OPENED_user_editor_request_401_3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setDescription("Description");
        editorRequest.setApplicant(editorUser);

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/list"),
                HttpMethod.GET, new HttpEntity<>(objectMapper.writeValueAsString(editorRequest), headers),
                Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_OPENED_user_editor_request_200_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/request/editor/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Set<EditorRequest> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<Set<EditorRequest>>(){});

        assertEquals(0, returnData.size());
    }

}
