package es.unizar.murcy.model.dto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class QuestionDtoTest {

    @Test
    public void question_DTO_getters_and_setters() {
        QuestionDto questionDto = new QuestionDto();

        questionDto.setId(1456L);
        assertEquals(1456L, questionDto.getId());

        questionDto.setTitle("title");
        assertEquals("title", questionDto.getTitle());

        questionDto.setOwnerUserName("me");
        assertEquals("me", questionDto.getOwnerUserName());

        questionDto.setOwnerId(5612L);
        assertEquals(5612L, questionDto.getOwnerId());

        questionDto.setDescription("description");
        assertEquals("description", questionDto.getDescription());

        questionDto.setMultiple(true);
        assertEquals(true, questionDto.isMultiple());

        WorkflowDto workflowDto = new WorkflowDto();
        workflowDto.setId(8469L);

        questionDto.setWorkflow(workflowDto);
        assertEquals(workflowDto, questionDto.getWorkflow());

        questionDto.setLastWorkflow(workflowDto);
        assertEquals(workflowDto, questionDto.getLastWorkflow());

        questionDto.setPublished(true);
        assertEquals(true, questionDto.isPublished());
    }
}