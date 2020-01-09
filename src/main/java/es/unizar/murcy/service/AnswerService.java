package es.unizar.murcy.service;

import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.repository.AnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AnswerService {

    @Autowired
    AnswerRepository answerRepository;

    public List<Answer> findAll() {
        return answerRepository.findAnswersByDeletedIsFalse();
    }

    public Optional<Answer> findById(long id) {
        return answerRepository.findByIdAndDeletedIsFalse(id);
    }

    public Optional<Answer> findByTitle(String title) {
        return answerRepository.findByTitleAndDeletedIsFalse(title);
    }

    public Answer update(Answer answer) {
        answer.setModifiedDate(new Date());
        return answerRepository.save(answer);
    }

    public Answer create(Answer answer) {
        return answerRepository.save(answer);
    }

    public boolean existsByTitle(String title) {
        return answerRepository.existsByTitleAndDeletedIsFalse(title);
    }


    public void delete(Answer answer) {
        answer.delete();
        update(answer);
    }

    public List<Answer> findAnswersByQuizId(Quiz quiz) {
        return findAnswersByOwnerId(quiz.getId());
    }

    public List<Answer> findAnswersByOwnerId(long quizId) {
        return answerRepository.findByDeletedIsFalseAndQuiz_id(quizId);
    }
}
