package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.service.QuestionService;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
public class QuizRequest {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description = "";

    @Getter
    @Setter
    private List<Long> questionsIds;

    @Getter
    @Setter
    private Boolean ordered;

    @Getter
    @Setter
    private Boolean publish;

    public Boolean isCreateValid() {
        if(publish != null && publish.equals(Boolean.TRUE)) {
            return this.title != null && !this.title.equals("") && this.questionsIds != null && !this.questionsIds.isEmpty();
        } else {
            return this.title != null && !this.title.equals("");
        }
    }

    public Quiz toEntity(QuestionService questionService) {
        Quiz quiz = new Quiz();
        quiz.setTitle(this.title);
        quiz.setDescription(this.description);
        quiz.setQuestionsOrdered(this.getOrdered());
        if(questionsIds != null && !questionsIds.isEmpty()) {
            quiz.setQuestions(questionService.findByIdsCollection(questionsIds));
        } else {
            quiz.setQuestions(new ArrayList<>());
        }
        return quiz;
    }
}
