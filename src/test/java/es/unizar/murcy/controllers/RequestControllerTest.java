package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.request.EditorRequestRequest;
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

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class RequestControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    UserService userService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private String randomToken;
    private String userUserToken;
    private User editorUser;
    private String editorUserToken;
    private User reviewerUser;
    private String reviewerUserToken;

    @Before
    public void setUp() {
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
        this.randomToken = userUserToken.substring(0, userUserToken.length() - 3).concat("aaa");
    }

    @Test
    public void test_GET_api_request_404_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/request/editor/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void test_POST_api_request_editor_401_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        EditorRequestRequest editorRequestRequest = new EditorRequestRequest();
        editorRequestRequest.setDescription("Description");

        ResponseEntity response = restTemplate.exchange(
                URI.create("http://localhost:" + port + "/api/request/editor/" + -1),
                HttpMethod.POST, new HttpEntity<>(objectMapper.writeValueAsString(editorRequestRequest), headers),
                Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_API_request_controller_201_1() throws Exception {

        EditorRequestRequest editorRequestRequest = new EditorRequestRequest();
        editorRequestRequest.setDescription("Description");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(editorRequestRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/request/editor", entity, Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

    }

    @Test
    public void test_API_request_controller_400_incomplete_1() {
        EditorRequestRequest editorRequestRequest = new EditorRequestRequest();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);
        String description;
        description = editorRequestRequest.getDescription();

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + description), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
