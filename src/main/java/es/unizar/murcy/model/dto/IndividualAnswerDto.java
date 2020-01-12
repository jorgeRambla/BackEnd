package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.ManyToOne;

@NoArgsConstructor
@AllArgsConstructor
public class IndividualAnswerDto {
    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private long resolutionTime;

    @Getter
    @Setter
    private int points;

    @Getter
    @Setter
    private long questionId;

    @Getter
    @Setter
    private LISTA ¡SIEMPRE DTO! answer;

    public IndividualAnswerDto(IndividualAnswer individualAnswer){
        this.id=individualAnswer.getId();
        this.answerText=individualAnswer.getAnswerText();
        this.resolutionTime=individualAnswer.getTimeInMillis();
        this.points=individualAnswer.getPoints();
        this.question=individualAnswer.getQuestion();
        this.answer=individualAnswer.getAnswer();
    }

}
