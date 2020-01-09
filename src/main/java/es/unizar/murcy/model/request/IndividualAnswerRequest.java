package es.unizar.murcy.model.request;

import es.unizar.murcy.model.IndividualAnswer;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IndividualAnswerRequest {


    @Getter
    @Setter
    private Integer timeInMillis;

    //ID de la pregunta,

    @Getter
    @Setter
    private Integer points;

    public Boolean isCreateValid() {
        return this.answerText != null && !this.answerText.equals("") &&this.points!= null;
    }

    public IndividualAnswer toEntity(long idAnswer) {
        IndividualAnswer individualAnswer=new IndividualAnswer();
        individualAnswer.setAnswerText(this.answerText);
        individualAnswer.setResolutionTime(this.resolutionTime);
        individualAnswer.setPoints(this.points);

        return individualAnswer;
    }

}
