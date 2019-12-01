package es.unizar.murcy.service;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.Workflow;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MurcyApplication.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WorkflowServiceTest {

    @Autowired
    WorkflowService workflowService;

    private Workflow workflow;

    @Before
    public void before() {
        Workflow workflow = new Workflow();
        workflow.setDescription("Test");
        workflow.setTitle("Test test");

        this.workflow = workflowService.create(workflow);
    }

    @Test
    public void testUpdate() {
        String prevDescription = workflow.getDescription();
        this.workflow.setDescription("new");

        Date prevDate = workflow.getModifiedDate();
        this.workflow = workflowService.update(this.workflow);

        assertNotEquals(prevDescription, this.workflow.getDescription());
        assertNotEquals(prevDate, this.workflow.getModifiedDate());

        assertEquals("new", this.workflow.getDescription());
        assertTrue(Math.abs(this.workflow.getModifiedDate().getTime() - prevDate.getTime()) < 100);

    }
}
