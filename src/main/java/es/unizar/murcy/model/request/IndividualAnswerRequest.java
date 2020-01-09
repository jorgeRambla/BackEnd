package es.unizar.murcy.model.request;

import es.unizar.murcy.model.IndividualAnswer;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IndividualAnswerRequest {

    @Getter
    @Setter
    private String answerText;

    @Getter
    @Setter
    private Integer resolutionTime=0;

    @Getter
    @Setter
    private Integer points;

    public Boolean isCreateValid() {
        return this.answerText != null && !this.answerText.equals("") &&this.points!= null;
    }

    public IndividualAnswer toEntity() {
        IndividualAnswer individualAnswer=new IndividualAnswer();
        individualAnswer.setAnswerText(this.answerText);
        individualAnswer.setResolutionTime(this.resolutionTime);
        individualAnswer.setPoints(this.points);

        return individualAnswer;
    }

}
