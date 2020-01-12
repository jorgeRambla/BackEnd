package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import lombok.Getter;
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
    private String answerText;

    @Getter
    @Setter
    private Integer resolutionTime;

    @Getter
    @Setter
    private Integer points;

    @ManyToOne
    @Getter
    @Setter
    private Question question;

    @ManyToOne
    @Getter
    @Setter
    private Answer answer;

    public IndividualAnswerDto(IndividualAnswer individualAnswer){
        this.id=individualAnswer.getId();
        this.answerText=individualAnswer.getAnswerText();
        this.resolutionTime=individualAnswer.getResolutionTime();
        this.points=individualAnswer.getPoints();
        this.question=individualAnswer.getQuestion();
        this.answer=individualAnswer.getAnswer();
    }

}
