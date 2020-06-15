package es.unizar.murcy.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.unizar.murcy.components.JsonWebTokenUtil;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.QuizDto;
import es.unizar.murcy.model.request.OptionRequest;
import es.unizar.murcy.model.request.QuestionRequest;
import es.unizar.murcy.model.request.QuizRequest;
import es.unizar.murcy.service.JwtUserDetailsService;
import es.unizar.murcy.service.QuestionService;
import es.unizar.murcy.service.QuizService;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class QuizControllerTest {

    @LocalServerPort
    private int port;

    @Autowired
    UserService userService;

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @Autowired
    private JsonWebTokenUtil jsonWebTokenUtil;

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuestionService questionService;

    private TestRestTemplate restTemplate = new TestRestTemplate();

    private ObjectMapper objectMapper = new ObjectMapper();

    private String randomToken;
    private String userUserToken;
    private User editorUser;
    private String editorUserToken;
    private User reviewerUser;
    private String reviewerUserToken;

    private Question question1;
    private Question question2;

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

        List<OptionRequest> options = new ArrayList<>();
        options.add(new OptionRequest("pregunta 1", false));
        options.add(new OptionRequest("pregunta 2", false));
        options.add(new OptionRequest("pregunta 3", false));
        options.add(new OptionRequest("pregunta 4", false));

        QuestionRequest questionRequest = new QuestionRequest("title", "description", options, false);

        this.question1 = questionService.create(questionRequest.toEntity());

        List<OptionRequest> options2 = new ArrayList<>();
        options2.add(new OptionRequest("pregunta 10", false));
        options2.add(new OptionRequest("pregunta 40", true));

        questionRequest = new QuestionRequest("title2", "description2", options2, false);

        this.question2 = questionService.create(questionRequest.toEntity());
    }

    @Test
    public void test_POST_API_QUIZ_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_401_3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        HttpEntity<String> entity = new HttpEntity<>(new ObjectMapper().writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_400_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_400_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest(null, "description", questionIds, true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_400_3() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("", "description", questionIds, true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_400_4() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        QuizRequest quizRequest = new QuizRequest("title", "description", null, true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_400_5() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        Set<Long> questionIds = new HashSet<>();

        QuizRequest quizRequest = new QuizRequest(null, "description", questionIds, true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_201_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_POST_API_QUIZ_201_2() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers);
        ResponseEntity response = restTemplate.postForEntity("http://localhost:" + port + "/api/quiz", entity, Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_401_3() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_200_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(0, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_200_2() throws Exception {
        test_POST_API_QUIZ_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_200_3() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list"), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_401_3() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + reviewerUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_200_1() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(0, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_200_2() throws Exception {
        test_POST_API_QUIZ_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_200_3() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_200_4() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + editorUser.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<QuizDto> returnData = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), new TypeReference<List<QuizDto>>(){});

        assertEquals(1, returnData.size());
    }

    @Test
    public void test_GET_API_QUIZ_LIST_ID_404_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/list/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_ID_401_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_ID_401_2() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + 1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_ID_EDITOR_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<Quiz> quizzes = quizService.findAll();
        for(Quiz quiz : quizzes) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
            if(quiz.getOwner().equals(editorUser)) {
                assertEquals(quiz.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class).getId());
                assertEquals(HttpStatus.OK, response.getStatusCode());
            } else {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }
    }

    @Test
    public void test_GET_API_QUIZ_ID_REVIEWER_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        List<Quiz> quizzes = quizService.findAll();
        for(Quiz quiz : quizzes) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
            assertEquals(quiz.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class).getId());
            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }

    @Test
    public void test_GET_API_QUIZ_ID_404_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_GET_API_QUIZ_ID_404_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.GET, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUIZ_ID_401_1() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUIZ_ID_401_2() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUIZ_ID_400_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<Quiz> quizzes = quizService.findAll();

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quizzes.get(0).getId()), HttpMethod.PUT, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUIZ_ID_404_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_PUT_API_QUIZ_ID_EDITOR_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        List<Quiz> quizzes = quizService.findAll();

        for(Quiz quiz : quizzes) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);
            if(quiz.getOwner().equals(editorUser)) {
                assertEquals(quiz.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class).getId());
                assertEquals(HttpStatus.CREATED, response.getStatusCode());
            } else {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_REVIEWER_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question1.getId());
        questionIds.add(question2.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        List<Quiz> quizzes = quizService.findAll();

        for(Quiz quiz : quizzes) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);
            assertEquals(quiz.getId(), objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class).getId());
            assertEquals(HttpStatus.CREATED, response.getStatusCode());
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_201_1() throws Exception {
        test_POST_API_QUIZ_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuizRequest quizRequest = new QuizRequest(null, null, null, null);

        Quiz quiz = quizService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuizDto quizDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class);
        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals(quiz.getTitle(), quizDto.getTitle());
        assertEquals(quiz.getDescription(), quizDto.getDescription());
        assertEquals(quiz.getOwner().getUsername(), quizDto.getOwnerUserName());
        assertEquals(quiz.getOwner().getId(), quizDto.getOwnerId());
        assertEquals(quiz.getQuestions().size(), quizDto.getQuestions().size());
        for(int iterator = 0; iterator < quizDto.getQuestions().size(); iterator++) {
            assertEquals(quiz.getQuestions().get(iterator).getId(), quizDto.getQuestions().get(iterator).getId());
            assertEquals(quiz.getQuestions().get(iterator).getTitle(), quizDto.getQuestions().get(iterator).getTitle());
            assertEquals(quiz.getQuestions().get(iterator).getIsMultiple(), quizDto.getQuestions().get(iterator).getIsMultiple());
            assertEquals(quiz.getQuestions().get(iterator).getDescription(), quizDto.getQuestions().get(iterator).getDescription());
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_201_2() throws Exception {
        test_POST_API_QUIZ_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuizRequest quizRequest = new QuizRequest(null, null, new HashSet<>(), null);

        Quiz quiz = quizService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuizDto quizDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class);
        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals(quiz.getTitle(), quizDto.getTitle());
        assertEquals(quiz.getDescription(), quizDto.getDescription());
        assertEquals(quiz.getOwner().getUsername(), quizDto.getOwnerUserName());
        assertEquals(quiz.getOwner().getId(), quizDto.getOwnerId());
        assertEquals(quiz.getQuestions().size(), quizDto.getQuestions().size());
        for(int iterator = 0; iterator < quizDto.getQuestions().size(); iterator++) {
            assertEquals(quiz.getQuestions().get(iterator).getId(), quizDto.getQuestions().get(iterator).getId());
            assertEquals(quiz.getQuestions().get(iterator).getTitle(), quizDto.getQuestions().get(iterator).getTitle());
            assertEquals(quiz.getQuestions().get(iterator).getIsMultiple(), quizDto.getQuestions().get(iterator).getIsMultiple());
            assertEquals(quiz.getQuestions().get(iterator).getDescription(), quizDto.getQuestions().get(iterator).getDescription());
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_201_3() throws Exception {
        test_POST_API_QUIZ_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuizRequest quizRequest = new QuizRequest("", null, new HashSet<>(), null);

        Quiz quiz = quizService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuizDto quizDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class);
        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals(quiz.getTitle(), quizDto.getTitle());
        assertEquals(quiz.getDescription(), quizDto.getDescription());
        assertEquals(quiz.getOwner().getUsername(), quizDto.getOwnerUserName());
        assertEquals(quiz.getOwner().getId(), quizDto.getOwnerId());
        assertEquals(quiz.getQuestions().size(), quizDto.getQuestions().size());
        for(int iterator = 0; iterator < quizDto.getQuestions().size(); iterator++) {
            assertEquals(quiz.getQuestions().get(iterator).getId(), quizDto.getQuestions().get(iterator).getId());
            assertEquals(quiz.getQuestions().get(iterator).getTitle(), quizDto.getQuestions().get(iterator).getTitle());
            assertEquals(quiz.getQuestions().get(iterator).getIsMultiple(), quizDto.getQuestions().get(iterator).getIsMultiple());
            assertEquals(quiz.getQuestions().get(iterator).getDescription(), quizDto.getQuestions().get(iterator).getDescription());
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_201_4() throws Exception {
        test_POST_API_QUIZ_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);


        Set<Long> questionIds = new HashSet<>();
        questionIds.add(question2.getId());
        questionIds.add(question1.getId());

        QuizRequest quizRequest = new QuizRequest("title", "description", questionIds, true);

        Quiz quiz = quizService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuizDto quizDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class);
        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals(quiz.getTitle(), quizDto.getTitle());
        assertEquals(quiz.getDescription(), quizDto.getDescription());
        assertEquals(quiz.getOwner().getUsername(), quizDto.getOwnerUserName());
        assertEquals(quiz.getOwner().getId(), quizDto.getOwnerId());
        assertEquals(questionIds.size(), quizDto.getQuestions().size());
        for(int iterator = 0; iterator < quizDto.getQuestions().size(); iterator++) {
            assertTrue(questionIds.contains(quiz.getQuestions().get(iterator).getId()));
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_201_5() throws Exception {
        test_POST_API_QUIZ_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuizRequest quizRequest = new QuizRequest("new", null, new HashSet<>(), true);

        Quiz quiz = quizService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuizDto quizDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class);
        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals("new", quizDto.getTitle());
        assertEquals(quiz.getDescription(), quizDto.getDescription());
        assertEquals(quiz.getOwner().getUsername(), quizDto.getOwnerUserName());
        assertEquals(quiz.getOwner().getId(), quizDto.getOwnerId());
        assertEquals(quiz.getQuestions().size(), quizDto.getQuestions().size());
        for(int iterator = 0; iterator < quizDto.getQuestions().size(); iterator++) {
            assertEquals(quiz.getQuestions().get(iterator).getId(), quizDto.getQuestions().get(iterator).getId());
            assertEquals(quiz.getQuestions().get(iterator).getTitle(), quizDto.getQuestions().get(iterator).getTitle());
            assertEquals(quiz.getQuestions().get(iterator).getIsMultiple(), quizDto.getQuestions().get(iterator).getIsMultiple());
            assertEquals(quiz.getQuestions().get(iterator).getDescription(), quizDto.getQuestions().get(iterator).getDescription());
        }
    }

    @Test
    public void test_PUT_API_QUIZ_ID_201_6() throws Exception {
        test_POST_API_QUIZ_201_1();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        QuizRequest quizRequest = new QuizRequest(null, "new", new HashSet<>(), true);

        Quiz quiz = quizService.findAll().get(0);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.PUT, new HttpEntity<>(objectMapper.writeValueAsString(quizRequest), headers), Object.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        // Compare result after update
        QuizDto quizDto = objectMapper.readValue(objectMapper.writeValueAsString(response.getBody()), QuizDto.class);
        assertEquals(quiz.getId(), quizDto.getId());
        assertEquals(quiz.getTitle(), quizDto.getTitle());
        assertEquals("new", quizDto.getDescription());
        assertEquals(quiz.getOwner().getUsername(), quizDto.getOwnerUserName());
        assertEquals(quiz.getOwner().getId(), quizDto.getOwnerId());
        assertEquals(quiz.getQuestions().size(), quizDto.getQuestions().size());
        for(int iterator = 0; iterator < quizDto.getQuestions().size(); iterator++) {
            assertEquals(quiz.getQuestions().get(iterator).getId(), quizDto.getQuestions().get(iterator).getId());
            assertEquals(quiz.getQuestions().get(iterator).getTitle(), quizDto.getQuestions().get(iterator).getTitle());
            assertEquals(quiz.getQuestions().get(iterator).getIsMultiple(), quizDto.getQuestions().get(iterator).getIsMultiple());
            assertEquals(quiz.getQuestions().get(iterator).getDescription(), quizDto.getQuestions().get(iterator).getDescription());
        }
    }

    @Test
    public void test_DELETE_API_QUIZ_ID_401_1() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(randomToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_DELETE_API_QUIZ_ID_401_2() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(userUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void test_DELETE_API_QUIZ_ID_404_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + -1), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_DELETE_API_QUIZ_ID_EDITOR_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(editorUserToken);

        List<Quiz> quizzes = quizService.findAll();

        for(Quiz quiz : quizzes) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
            if(quiz.getOwner().equals(editorUser)) {
                assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
            } else {
                assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
            }
        }
    }

    @Test
    public void test_DELETE_API_QUIZ_ID_REVIEWER_1() throws Exception {
        test_POST_API_QUIZ_201_1();
        test_POST_API_QUIZ_201_2();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(reviewerUserToken);

        List<Quiz> quizzes = quizService.findAll();

        for(Quiz quiz : quizzes) {
            ResponseEntity response = restTemplate.exchange(URI.create("http://localhost:" + port + "/api/quiz/" + quiz.getId()), HttpMethod.DELETE, new HttpEntity<>(headers), Object.class);
            assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
        }

        assertEquals(0, quizService.findAll().size());
    }
}