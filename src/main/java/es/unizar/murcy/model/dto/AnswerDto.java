package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Answer;
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
    private Integer resolutionTimeInMillis;

    @Getter
    @Setter
    private Integer totalPoints;

    @Getter
    @Setter
    private long quizId;

    @Getter
    @Setter
    private List<IndividualAnswerDto> individualAnswers;

    public AnswerDto(Answer answer) {
        this.id=answer.getId();
        this.totalPoints=answer.getTotalPoints();
        this.quizId=answer.getQuiz().getId();
        this.individualAnswers = answer.getIndividualAnswers().stream().map(IndividualAnswerDto::new).collect(Collectors.toList());
    }

}
