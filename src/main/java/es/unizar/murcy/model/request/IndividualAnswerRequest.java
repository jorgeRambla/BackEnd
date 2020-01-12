package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.QuestionService;
import lombok.*;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class IndividualAnswerRequest {

    @Getter
    @Setter
    private long timeInMillis;

    @Getter
    @Setter
    private Integer points;

    @Getter
    @Setter
    private long questionId;

    @Getter
    @Setter
    private Set<Long> answerIds;

    public Boolean isCreateValid() {
        return this.points!= null && this.answerId!=0 && this.questionId!=0;
    }
    //FIXME: CHECK answerIds VALIDITY WITH CURRENT CHANGES,
    // 4 <= answersIds.len => 0 if !ismultilple, else answerIds.len == 1

    public IndividualAnswer toEntity(AnswerService answerService, QuestionService questionService) {
        IndividualAnswer individualAnswer=new IndividualAnswer();
        individualAnswer.setTimeInMillis(this.timeInMillis);
        individualAnswer.setPoints(this.points);
        Optional<Question> questionOptional=questionService.findById(questionId);
        Optional<Answer> answerOptional=answerService.findById(answerId);

        if(questionOptional.isPresent()){
            individualAnswer.setQuestion(questionOptional.get());
        }

        if(answerOptional.isPresent()){
            individualAnswer.setAnswer(answerOptional.get());
        }

        Optional<Question> optionalQuestion = questionService.findById(questionId);
        optionalQuestion.ifPresent(individualAnswer::setQuestion);

        //FIXME: SET CURRENT VALUE TO INDIVIDUAL ANSWER OBJECT
        answerIds.stream().map(answerService::findById).filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
        return individualAnswer;
    }

}
