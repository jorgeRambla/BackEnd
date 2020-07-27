package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.service.QuestionService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class AnswerDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private long userId;

    @Getter
    @Setter
    private long quizId;

    @Getter
    @Setter
    private long timeInMillis;

    @Getter
    @Setter
    private long totalPoints;

    @Getter
    @Setter
    private List<IndividualAnswerDto> individualAnswers;

    public AnswerDto(Answer answer, QuestionService questionService) {
        this.id = answer.getId();
        this.userId = answer.getUser().getId();
        this.quizId = answer.getQuiz().getId();
        this.timeInMillis = answer.getTimeInMillis();
        this.totalPoints = answer.getTotalPoints();
        this.individualAnswers = answer.getIndividualAnswers().stream().map((item) -> new IndividualAnswerDto(item, questionService)).collect(Collectors.toList());
    }

}
