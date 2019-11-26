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
    public void testSetIdQuestion(){
        Question question=new Question();
        question.setId(1);
        assertEquals(question.getId(), 1);
    }

    @Test
    public void testGetIdQuestion(){
        Question question=new Question();
        question.setId(1);
        long id=question.getId();
        assertEquals(id,1);
    }

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
        question.setUser(newUser);
        User testUser=question.getUser();
        assertEquals(testUser.getUsername(),"Test");
    }

    @Test
    public void testGetUserQuestion(){
        Question question=new Question();
        User newUser= new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        question.setUser(newUser);
        assertEquals(question.getUser().getUsername(),"Test");
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
    public void testSetCreateDateQuestion(){
        Question question=new Question();
        Date date=new Date();
        question.setCreateDate(date);
        assertEquals(question.getCreateDate(),date);
    }

    @Test
    public void testGetCreateDateQuestion(){
        Question question=new Question();
        Date date=new Date();
        question.setCreateDate(date);
        Date testDate=question.getCreateDate();
        assertEquals(testDate,date);
    }

    @Test
    public void testSetModifiedDateQuestion(){
        Question question=new Question();
        Date date=new Date();
        question.setModifiedDate(date);
        assertEquals(question.getModifiedDate(),date);
    }

    @Test
    public void testGetModifiedDateQuestion(){
        Question question=new Question();
        Date date=new Date();
        question.setModifiedDate(date);
        Date testDate=question.getModifiedDate();
        assertEquals(testDate,date);
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
    public void testSetClosedQuestion() {
        Question question=new Question();
        question.setClosed(true);
        boolean testMultiple=question.getClosed();
        assertEquals(testMultiple,true);
    }

    @Test
    public void testGetClosedQuestion() {
        Question question=new Question();
        question.setClosed(true);
        boolean closed = question.getClosed();
        assertEquals(closed,true);
    }

    @Test
    public void testSetApprovedQuestion() {
        Question question=new Question();
        question.setApproved(true);
        boolean testMultiple=question.getApproved();
        assertEquals(testMultiple,true);
    }

    @Test
    public void testGetApprovedQuestion() {
        Question question=new Question();
        question.setApproved(true);
        boolean approved =question.getApproved();
        assertEquals(approved,true);
    }

    @Test
    public void testSetListQuestion() {
        Question question=new Question();
        Option option=new Option();
        option.setCorrect(true);
        option.setId(1);
        option.setText("Opcion 1");
        List<Option> list= new ArrayList<>();
        list.add(option);
        question.setOptions(list);
        List<Option> listTest=question.getOptions();
        Option optionTest=listTest.get(0);
        assertEquals(optionTest.getId(),1);
    }

    @Test
    public void testGetListQuestion() {
        Question question=new Question();
        Option option=new Option();
        option.setCorrect(true);
        option.setId(1);
        option.setText("Opcion 1");
        List<Option> list= new ArrayList<>();
        list.add(option);
        question.setOptions(list);
        List<Option> listTest=question.getOptions();
        Option optionTest=listTest.get(0);
        assertEquals(optionTest.getId(),1);
    }


}
