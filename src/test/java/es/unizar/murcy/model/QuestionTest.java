package es.unizar.murcy.model;


import es.unizar.murcy.MurcyApplication;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class QuestionTest {
    @Test
    public void testSetTitleQuestion(){
        Question question=new Question();
        question.setTitle("Pregunta");
        assertEquals(question.getTitle(),"Pregunta");
    }

    @Test
    public void testGetTitleQuestion(){
        Question question=new Question();
        question.setTitle("PreguntaPrueba");
        String title = question.getTitle();
        assertEquals(title , "PreguntaPrueba");
    }

    @Test
    public void testSetUserQuestion(){
        Question question=new Question();
        User newUser= new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        question.setOwner(newUser);
        User testUser=question.getOwner();
        assertEquals(testUser.getUsername(),"Test");
    }

    @Test
    public void testGetUserQuestion(){
        Question question=new Question();
        User newUser= new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        question.setOwner(newUser);
        assertEquals(question.getOwner().getUsername(),"Test");
    }

    @Test
    public void testGetDescriptionQuestion(){
        Question question=new Question();
        question.setDescription("Descripcion");
        assertEquals(question.getDescription(),"Descripcion");
    }

    @Test
    public void testSetDescriptionQuestion(){
        Question question=new Question();
        question.setDescription("Descripcion");
        String description=question.getDescription();
        assertEquals(description,"Descripcion");
    }

    @Test
    public void testSetIsMultipleQuestion() {
        Question question=new Question();
        question.setIsMultiple(true);
        boolean testMultiple=question.getIsMultiple();
        assertEquals(testMultiple,true);
    }

    @Test
    public void testGetIsMultipleQuestion() {
        Question question=new Question();
        question.setIsMultiple(true);
        boolean multiple=question.getIsMultiple();
        assertEquals(multiple,true);
    }

    @Test
    public void testSetWorkflowQuestion() {
        Question question=new Question();
        Workflow workflow=new Workflow();
        question.setWorkflow(workflow);
        Workflow testWorkflow=question.getWorkflow();
        assertEquals(testWorkflow,workflow);
    }

    @Test
    public void testGetWorkflowQuestion() {
        Question question=new Question();
        Workflow workflow=new Workflow();
        question.setWorkflow(workflow);
        assertEquals(question.getWorkflow(),workflow);
    }

    @Test
    public void testSetLastWorkflowQuestion() {
        Question question=new Question();
        Workflow workflow=new Workflow();
        question.setLastWorkflow(workflow);
        Workflow testWorkflow=question.getLastWorkflow();
        assertEquals(testWorkflow,workflow);
    }

    @Test
    public void testGetLastWorkflowQuestion() {
        Question question=new Question();
        Workflow workflow=new Workflow();
        question.setLastWorkflow(workflow);
        assertEquals(question.getLastWorkflow(),workflow);
    }

    @Test
    public void testSetListQuestion() {
        Question question=new Question();
        Option option=new Option();
        option.setCorrect(true);
        option.setTitle("option1");
        List<Option> list= new ArrayList<>();
        list.add(option);
        question.setOptions(list);
        List<Option> listTest=question.getOptions();
        Option optionTest=listTest.get(0);
        assertEquals(optionTest.getTitle(),"option1");
    }

    @Test
    public void testGetListQuestion() {
        Question question=new Question();
        Option option=new Option();
        option.setCorrect(true);
        option.setTitle("option1");
        List<Option> list= new ArrayList<>();
        list.add(option);
        question.setOptions(list);
        List<Option> listTest=question.getOptions();
        Option optionTest=listTest.get(0);
        assertEquals(optionTest.getTitle(),"option1");
    }


}
