package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.QuestionDto;
import es.unizar.murcy.model.request.OptionRequest;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.QuestionService;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class QuestionControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    UserService userService;

    @Autowired
    QuestionService questionService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

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
    public void test_POST_API_QUESTION_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_401_3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", true));

        QuestionRequest questionRequest = new QuestionRequest("title", "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_400_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_400_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", true));

        QuestionRequest questionRequest = new QuestionRequest(null, "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_400_3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", true));

        QuestionRequest questionRequest = new QuestionRequest("", "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_400_4() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));

        QuestionRequest questionRequest = new QuestionRequest("title", "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_400_5() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", false));
        options.add(new OptionRequest("pregunta 3", false));
        options.add(new OptionRequest("pregunta 4", false));
        options.add(new OptionRequest("pregunta 5", false));


        QuestionRequest questionRequest = new QuestionRequest("title", "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_201_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", false));
        options.add(new OptionRequest("pregunta 3", false));
        options.add(new OptionRequest("pregunta 4", false));

        QuestionRequest questionRequest = new QuestionRequest("title", "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUESTION_201_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", false));
        options.add(new OptionRequest("pregunta 3", false));
        options.add(new OptionRequest("pregunta 4", false));

        QuestionRequest questionRequest = new QuestionRequest("title", "description", options, false);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/question", entity, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_401_3() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_200_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(0, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_200_2() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_200_3() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_401_3() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }


    @Test
    public void test_GET_API_QUESTION_LIST_ID_200_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(0, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_200_2() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_200_3() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_200_4() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuestionDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuestionDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUESTION_LIST_ID_404_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/list/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_ID_401_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_ID_401_2() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + 1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_ID_EDITOR_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<Question> questions = questionService.findAll();
        for(Question question : questions) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
            if(question.getOwner().equals(editorUser)) {
                assertEquals(question.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class).getId());
                assertEquals(HttpStatus.OK, response.getStatusCode());
            } else {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }
    }

    @Test
    public void test_GET_API_QUESTION_ID_REVIEWER_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        List<Question> questions = questionService.findAll();
        for(Question question : questions) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
            assertEquals(question.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class).getId());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void test_GET_API_QUESTION_ID_404_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUESTION_ID_404_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_401_1() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", false));
        options.add(new OptionRequest("pregunta 40", true));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_401_2() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", false));
        options.add(new OptionRequest("pregunta 40", true));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_400_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<Question> questions = questionService.findAll();

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + questions.get(0).getId()), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_400_2() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        List<Question> questions = questionService.findAll();

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + questions.get(0).getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_400_3() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", false));
        options.add(new OptionRequest("pregunta 30", false));
        options.add(new OptionRequest("pregunta 40", true));
        options.add(new OptionRequest("pregunta 50", true));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        List<Question> questions = questionService.findAll();

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + questions.get(0).getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_404_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", false));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUESTION_ID_EDITOR_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", false));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        List<Question> questions = questionService.findAll();

        for(Question question : questions) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
            if(question.getOwner().equals(editorUser)) {
                assertEquals(question.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class).getId());
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
            } else {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }
    }

    @Test
    public void test_PUT_API_QUESTION_ID_REVIEWER_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", false));

        QuestionRequest questionRequest = new QuestionRequest("new", "desc", options, false);

        List<Question> questions = questionService.findAll();

        for(Question question : questions) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);
            assertEquals(question.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class).getId());
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
    }

    @Test
    public void test_PUT_API_QUESTION_ID_201_1() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest(null, null, null, false);

        Question question = questionService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getIsMultiple(), questionDto.isMultiple());
        assertEquals(question.getOwner().getUsername(), questionDto.getOwnerUserName());
        assertEquals(question.getOptions().size(), questionDto.getOptions().size());
        for(int iterator = 0; iterator < questionDto.getOptions().size(); iterator++) {
            assertEquals(question.getOptions().get(iterator).getTitle(), questionDto.getOptions().get(iterator).getTitle());
            assertEquals(question.getOptions().get(iterator).getCorrect(), questionDto.getOptions().get(iterator).isCorrect());
        }
    }

    @Test
    public void test_PUT_API_QUESTION_ID_201_2() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest(null, null, new ArrayList<>(), false);

        Question question = questionService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getIsMultiple(), questionDto.isMultiple());
        assertEquals(question.getOwner().getUsername(), questionDto.getOwnerUserName());
        assertEquals(question.getOptions().size(), questionDto.getOptions().size());
        for(int iterator = 0; iterator < questionDto.getOptions().size(); iterator++) {
            assertEquals(question.getOptions().get(iterator).getTitle(), questionDto.getOptions().get(iterator).getTitle());
            assertEquals(question.getOptions().get(iterator).getCorrect(), questionDto.getOptions().get(iterator).isCorrect());
        }
    }

    @Test
    public void test_PUT_API_QUESTION_ID_201_3() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);


        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 10", true));
        options.add(new OptionRequest("pregunta 20", true));

        QuestionRequest questionRequest = new QuestionRequest(null, null, options, false);

        Question question = questionService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getIsMultiple(), questionDto.isMultiple());
        assertEquals(question.getOwner().getUsername(), questionDto.getOwnerUserName());
        assertEquals(2, questionDto.getOptions().size());
        for(int iterator = 0; iterator < questionDto.getOptions().size(); iterator++) {
            assertNotEquals(question.getOptions().get(iterator).getTitle(), questionDto.getOptions().get(iterator).getTitle());
            assertNotEquals(question.getOptions().get(iterator).getCorrect(), questionDto.getOptions().get(iterator).isCorrect());
        }
    }

    @Test
    public void test_PUT_API_QUESTION_ID_201_4() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest("new", null, null, false);

        Question question = questionService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getId(), questionDto.getId());
        assertNotEquals(question.getTitle(), questionDto.getTitle());
        assertEquals("new", questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getIsMultiple(), questionDto.isMultiple());
        assertEquals(question.getOwner().getUsername(), questionDto.getOwnerUserName());
        assertEquals(question.getOptions().size(), questionDto.getOptions().size());
        for(int iterator = 0; iterator < questionDto.getOptions().size(); iterator++) {
            assertEquals(question.getOptions().get(iterator).getTitle(), questionDto.getOptions().get(iterator).getTitle());
            assertEquals(question.getOptions().get(iterator).getCorrect(), questionDto.getOptions().get(iterator).isCorrect());
        }
    }
    @Test
    public void test_PUT_API_QUESTION_ID_201_5() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest(null, "new", null, false);

        Question question = questionService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertNotEquals(question.getDescription(), questionDto.getDescription());
        assertEquals("new", questionDto.getDescription());
        assertEquals(question.getIsMultiple(), questionDto.isMultiple());
        assertEquals(question.getOwner().getUsername(), questionDto.getOwnerUserName());
        assertEquals(question.getOptions().size(), questionDto.getOptions().size());
        for(int iterator = 0; iterator < questionDto.getOptions().size(); iterator++) {
            assertEquals(question.getOptions().get(iterator).getTitle(), questionDto.getOptions().get(iterator).getTitle());
            assertEquals(question.getOptions().get(iterator).getCorrect(), questionDto.getOptions().get(iterator).isCorrect());
        }
    }

    @Test
    public void test_PUT_API_QUESTION_ID_201_6() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest("", null, null, false);

        Question question = questionService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getId(), questionDto.getId());
        assertEquals(question.getTitle(), questionDto.getTitle());
        assertEquals(question.getDescription(), questionDto.getDescription());
        assertEquals(question.getIsMultiple(), questionDto.isMultiple());
        assertEquals(question.getOwner().getUsername(), questionDto.getOwnerUserName());
        assertEquals(question.getOptions().size(), questionDto.getOptions().size());
        for(int iterator = 0; iterator < questionDto.getOptions().size(); iterator++) {
            assertEquals(question.getOptions().get(iterator).getTitle(), questionDto.getOptions().get(iterator).getTitle());
            assertEquals(question.getOptions().get(iterator).getCorrect(), questionDto.getOptions().get(iterator).isCorrect());
        }
    }

    /**
     * Given closed question
     * When updated
     * Then new pending workflow is created
     * @throws Exception
     */
    @Test
    public void test_PUT_API_QUESTION_ID_201_7() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest("title", null, null, false);

        Question question = questionService.findAll().get(0);
        question.setApproved(true);
        question.setClosed(true);
        question = questionService.update(question);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertNotEquals(question.getLastWorkflow().getId(), questionDto.getLastWorkflow().getId());
    }

    /**
     * Given opened question
     * When updated
     * Then not new workflow is created
     * @throws Exception
     */
    @Test
    public void test_PUT_API_QUESTION_ID_201_8() throws Exception {
        test_POST_API_QUESTION_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuestionRequest questionRequest = new QuestionRequest("title", null, null, false);

        Question question = questionService.findAll().get(0);
        question.setApproved(false);
        question.setClosed(false);
        question = questionService.update(question);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(questionRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuestionDto questionDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuestionDto.class);
        assertEquals(question.getLastWorkflow().getId(), questionDto.getLastWorkflow().getId());
    }

    @Test
    public void test_DELETE_API_QUESTION_ID_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_DELETE_API_QUESTION_ID_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_DELETE_API_QUESTION_ID_404_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + -1), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_DELETE_API_QUESTION_ID_EDITOR_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<Question> questions = questionService.findAll();

        for(Question question : questions) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
            if(question.getOwner().equals(editorUser)) {
                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            } else {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }
    }

    @Test
    public void test_DELETE_API_QUESTION_ID_REVIEWER_1() throws Exception {
        test_POST_API_QUESTION_201_1();
        test_POST_API_QUESTION_201_2();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        List<Question> questions = questionService.findAll();

        for(Question question : questions) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/question/" + question.getId()), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        }

        assertEquals(0, questionService.findAll().size());
    }
}