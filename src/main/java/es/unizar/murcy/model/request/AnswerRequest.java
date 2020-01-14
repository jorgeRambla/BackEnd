package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.service.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    @Getter
    @Setter
    private List<IndividualAnswerRequest> individualAnswers = new ArrayList<>();

    public Boolean isCreateValid(Quiz quiz) {
        return !individualAnswers.isEmpty() && quiz.getQuestions().size() == individualAnswers.size();
    }

    public Answer toEntity(QuestionService questionService) {
        Answer answer = new Answer();

        answer.setIndividualAnswers(
                individualAnswers
                        .stream()
                        .filter(individualAnswer -> individualAnswer.isCreateValid(questionService))
                        .map(item -> item.toEntity(questionService))
                        .collect(Collectors.toList()));

        answer.getIndividualAnswers()
                .stream()
                .map(IndividualAnswer::getTimeInMillis)
                .reduce(Long::sum)
                .ifPresent(answer::setTimeInMillis);

        answer.getIndividualAnswers()
                .stream()
                .map(IndividualAnswer::getPoints)
                .reduce(Long::sum)
                .ifPresent(answer::setTotalPoints);

        return answer;
    }

}