package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.service.QuestionService;
import lombok.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class QuizRequest {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description = "";

    @Getter
    @Setter
    private Set<Long> questionIds;

    public Boolean isCreateValid() {
        return this.title != null && !this.title.equals("") && this.questionIds != null && !this.questionIds.isEmpty();
    }

    public Quiz toEntity(QuestionService questionService) {
        Quiz quiz = new Quiz();
        quiz.setTitle(this.title);
        quiz.setDescription(this.description);
        quiz.setQuestions(
                questionIds
                        .stream()
                        .map(questionService::findById)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList()));
        return quiz;
    }
}