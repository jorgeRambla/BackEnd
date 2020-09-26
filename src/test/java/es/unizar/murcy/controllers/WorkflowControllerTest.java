package es.unizar.murcy.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.model.request.UpdateWorkflowStatusRequest;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.UserService;
import es.unizar.murcy.service.WorkflowService;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WorkflowControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String randomToken;
    private String userUserToken;
    private User editorUser;
    private String editorUserToken;
    private User reviewerUser;
    private String reviewerUserToken;

    private Question question;

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

        List<Option> optionList = new ArrayList<>();
        optionList.add(new Option("question1", false));
        optionList.add(new Option("question2", false));

        Question question = new Question();
        question.setTitle("title");
        question.setDescription("description");
        question.setIsMultiple(false);
        question.setOwner(reviewerUser);
        question.setOptions(optionList);

        Workflow workflow = new Workflow();
        workflow.setDescription("description");
        workflow.setStatusUser(null);
        workflow.setTitle("Solicitud publicar pregunta");

        question.setWorkflow(workflow);
        question.setLastWorkflow(workflow);

        question = questionService.create(question);
        this.question = question;
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_401_3() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_401_4() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_400_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_400_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest(null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_404_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("response");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_201_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(question.getLastWorkflow().getId()))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Question> question = questionService.findById(this.question.getId());

        assertTrue(question.isPresent());

        assertEquals("", question.get().getLastWorkflow().getResponse());
        assertTrue(question.get().isClosed());
        assertFalse(question.get().isApproved());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_DENY_201_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("response");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/deny".replace("{ID}", Long.toString(question.getLastWorkflow().getId()))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Question> question = questionService.findById(this.question.getId());

        assertTrue(question.isPresent());

        assertEquals("response", question.get().getLastWorkflow().getResponse());
        assertTrue(question.get().isClosed());
        assertFalse(question.get().isApproved());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_401_3() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_401_4() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_400_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_400_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest(null);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_404_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("response");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(-1L))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_201_1() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(question.getLastWorkflow().getId()))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Question> question = questionService.findById(this.question.getId());

        assertTrue(question.isPresent());

        assertEquals("", question.get().getLastWorkflow().getResponse());
        assertTrue(question.get().isClosed());
        assertTrue(question.get().isApproved());
    }

    @Test
    public void test_PUT_API_WORKFLOW_ID_APPROVE_201_2() throws Exception{
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        UpdateWorkflowStatusRequest updateWorkflowStatusRequest = new UpdateWorkflowStatusRequest("response");

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/workflow/{ID}/approve".replace("{ID}", Long.toString(question.getLastWorkflow().getId()))), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(updateWorkflowStatusRequest), headers), Object.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        Optional<Question> question = questionService.findById(this.question.getId());

        assertTrue(question.isPresent());

        assertEquals("response", question.get().getLastWorkflow().getResponse());
        assertTrue(question.get().isClosed());
        assertTrue(question.get().isApproved());
    }
}