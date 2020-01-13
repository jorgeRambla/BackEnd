package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.QuestionService;
import lombok.*;

import java.util.List;
import java.util.Optional;
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
    private long answerId;

    @Getter
    @Setter
    private List<OptionRequest> options;

    public Boolean isCreateValid(QuestionService questionService) {
        Optional<Question> questionOptional= questionService.findById(this.questionId);
        if(questionOptional.isPresent()){
            if(Boolean.TRUE.equals(questionOptional.get().getIsMultiple())){
                return this.points!= null && this.options!=null && this.options.size()<=4
                        && this.questionId!=0;
            }else{
                return this.options.size()==1;
            }
        }
        else{
            return false;
        }
    }

    public IndividualAnswer toEntity(AnswerService answerService, QuestionService questionService) {
        IndividualAnswer individualAnswer=new IndividualAnswer();
        individualAnswer.setTimeInMillis(this.timeInMillis);
        individualAnswer.setPoints(this.points);
        Optional<Question> questionOptional=questionService.findById(questionId);
        Optional<Answer> answerOptional=answerService.findById(answerId);

        questionOptional.ifPresent(individualAnswer::setQuestion);

        answerOptional.ifPresent(individualAnswer::setAnswer);

        individualAnswer.setOptions(options.stream().map(OptionRequest::toEntity).collect(Collectors.toList()));

        return individualAnswer;
    }

}
