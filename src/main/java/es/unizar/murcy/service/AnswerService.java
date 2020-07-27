package es.unizar.murcy.service;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.repository.AnswerRepository;
import es.unizar.murcy.repository.IndividualAnswerRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class AnswerService {

    private final AnswerRepository answerRepository;
    private final IndividualAnswerRepository individualAnswerRepository;

    public AnswerService(AnswerRepository answerRepository, IndividualAnswerRepository individualAnswerRepository) {
        this.answerRepository = answerRepository;
        this.individualAnswerRepository = individualAnswerRepository;
    }

    public List<Answer> findAll() {
        return answerRepository.findAnswersByDeletedIsFalse();
    }

    public Optional<Answer> findById(long id) {
        return answerRepository.findByIdAndDeletedIsFalse(id);
    }

    public Answer update(Answer answer) {
        answer.setModifiedDate(new Date());
        return answerRepository.save(answer);
    }

    public Answer create(Answer answer) {
        return answerRepository.save(answer);
    }

    public void delete(Answer answer) {
        answer.delete();
        update(answer);
    }

    public List<Answer> findAnswersByQuizId(long quizId) {
        return answerRepository.findByDeletedIsFalseAndQuiz_id(quizId);
    }

    public Set<Answer> findAnswersByUser(long id) {
        return answerRepository.findAnswerByUser_IdAndDeletedIsFalse(id);
    }
}
