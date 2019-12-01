package es.unizar.murcy.service;

import es.unizar.murcy.MurcyApplication;
import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
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
public class EditorRequestServiceTest {

    @Autowired
    EditorRequestService editorRequestService;

    @Autowired
    UserService userService;

    private EditorRequest editorRequest;

    @Before
    public void before() {
        User newUser = new User("Test", "testpass", "test@test.com", "Test Test");
        newUser.addRol(User.Rol.USER);
        newUser = userService.create(newUser);

        EditorRequest editorRequest = new EditorRequest();
        editorRequest.setApplicant(newUser);
        editorRequest.setDescription("test");

        this.editorRequest = editorRequestService.create(editorRequest);
    }

    @Test
    public void testFindEditorRequestByApplicant() {
        assertTrue(editorRequestService.findEditorRequestByApplicant(editorRequest.getApplicant()).isPresent());
    }

    @Test
    public void testUpdate() {
        editorRequest.setDescription("new");
        Date prevDate = editorRequest.getModifiedDate();
        editorRequest = editorRequestService.update(editorRequest);

        assertEquals("new", editorRequest.getDescription());
        assertNotEquals(prevDate, editorRequest.getModifiedDate());

        assertTrue(Math.abs(editorRequest.getModifiedDate().getTime() - prevDate.getTime()) < 50);
    }

    @Test
    public void testFindByClosedAndApproved() {
        assertEquals(0, editorRequestService.findByClosedAndApproved(true, true).size());
        assertEquals(1, editorRequestService.findByClosedAndApproved(false, false).size());
        assertEquals(0, editorRequestService.findByClosedAndApproved(true, false).size());
        assertEquals(0, editorRequestService.findByClosedAndApproved(false, true).size());
    }
}
