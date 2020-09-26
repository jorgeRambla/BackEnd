package es.unizar.murcy.model.request;

import es.unizar.murcy.exceptions.question.QuestionNotFoundException;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.service.QuestionService;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
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
    private long questionId;

    @Getter
    @Setter
    private List<Long> optionsIds = new ArrayList<>();

    public Boolean isCreateValid(QuestionService questionService) {
        Question question = questionService.findById(this.questionId).orElseThrow(QuestionNotFoundException::new);

        if(Boolean.TRUE.equals(question.getIsMultiple())){
            return this.optionsIds.size()<=4 && this.questionId > 0;
        }else{
            return this.optionsIds.size() == 1;
        }
    }

    private boolean isCorrect(Question question) {
        long correctAnswers = question.getOptions().stream().filter(Option::getCorrect).count();

        if(correctAnswers == optionsIds.size()) {
            return optionsIds
                    .stream()
                    .distinct()
                    .filter(
                            question.getOptions()
                                    .stream()
                                    .filter(Option::getCorrect)
                                    .mapToLong(Option::getId)
                                    .boxed()
                                    .collect(Collectors.toList())::contains)
                    .count() == correctAnswers;
        }
        return false;
    }

    public IndividualAnswer toEntity(QuestionService questionService) {
        IndividualAnswer individualAnswer = new IndividualAnswer();
        Question question = questionService.findById(questionId).orElseThrow(QuestionNotFoundException::new);


        individualAnswer.setTimeInMillis(this.timeInMillis);
        individualAnswer.setPoints(isCorrect(question) ? 1 : 0);

        individualAnswer.setOptionsIds(
                question.getOptions()
                        .stream()
                        .filter(option -> optionsIds.contains(option.getId()))
                        .map(Option::getId)
                        .collect(Collectors.toList()));

        return individualAnswer;
    }

}
