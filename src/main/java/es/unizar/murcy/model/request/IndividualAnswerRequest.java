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

    @Getter
    @Setter
    private Integer points;

    public Boolean isCreateValid() {
        return this.points!= null;
    }

    //Una respuesta individual ira asociada a una respusta general, se necesitara que pasen el id de la respuesta
    // en la peticion... mas
    public IndividualAnswer toEntity(long idAnswer) {
        IndividualAnswer individualAnswer=new IndividualAnswer();
        individualAnswer.setPoints(this.points);

        return individualAnswer;
    }

}
