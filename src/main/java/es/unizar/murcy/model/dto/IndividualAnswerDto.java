package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.Question;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.ManyToOne;

public class IndividualAnswerDto {

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

    public IndividualAnswerDto(){

    }

}
