package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.QuestionService;
import lombok.*;

import java.util.Optional;

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
    private long answerId;

    public Boolean isCreateValid() {
        return this.points!= null && this.answerId!=0 && this.questionId!=0;
    }

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


        return individualAnswer;
    }

}
