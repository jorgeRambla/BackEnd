package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuizService;
import es.unizar.murcy.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class AnswerRequest {

    @Getter
    @Setter
    private long idUser;

    @Getter
    @Setter
    private long quizId;

    @Getter
    @Setter
    private List<IndividualAnswerRequest> individualAnswers;

    public Boolean isCreateValid(QuizService quizService) {
        Optional<Quiz> quizOptional=quizService.findById(quizId);
        if(quizOptional.isPresent()&&individualAnswers!=null&&!individualAnswers.isEmpty()){
            return quizOptional.get().getQuestions().size()==individualAnswers.size();
        }
        else{
            return false;
        }
    }

    public Answer toEntity(IndividualAnswerService individualAnswerService, UserService userService, QuizService quizService) {
        Answer answer=new Answer();
        answer.setUser(userService.findUserById(this.idUser).get());
        answer.setQuiz(quizService.findById(this.quizId).get());
        answer.setIndividualAnswers(individualAnswers);

                .map(questionService::findById)
                .filter(Optional::isPresent)

                .collect(Collectors.toList()));
        return answer;
    }

}