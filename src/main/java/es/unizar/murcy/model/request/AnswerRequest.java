package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.dto.IndividualAnswerDto;
import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import es.unizar.murcy.service.IndividualAnswerService;
import lombok.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    //TODO: ADD IDUSUARIO
    //TODO: ADD QUIZID

    @Getter
    @Setter
    private List<IndividualAnswerRequest> individualAnswers;

    public Boolean isCreateValid() {
        return this.title != null && !this.title.equals("")
                && this.individualAnswersIds != null && !this.individualAnswersIds.isEmpty();
        //FIXME: quiz exists && quiz.len == individualA.len
    }

    public Answer toEntity(IndividualAnswerService individualAnswerService) {
        Answer answer=new Answer();
        answer.setTitle(this.title);
        answer.setDescription(this.description);
        answer.setIndividualAnswers(individualAnswersIds.stream()
                .map(individualAnswerService::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList()));
        return answer;
    }

}