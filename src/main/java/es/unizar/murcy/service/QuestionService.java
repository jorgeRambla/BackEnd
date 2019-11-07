package es.unizar.murcy.service;

import es.unizar.murcy.model.Question;
import es.unizar.murcy.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public List<Question> findAllQuestions() {
        return questionRepository.findAll();
    }

    public Optional<Question> findQuestionById(long id) {
        return questionRepository.findById(id);
    }

    public Optional<Question> findQuestionByTitle(String title) {
        return questionRepository.findQuestionByTitle(title);
    }

    public Question update(Question q) {
        q.setModifiedDate(new Date());
        return questionRepository.save(q);
    }

    public Question createQuestion(Question p) {
        return questionRepository.save(p);
    }

    public boolean existsByTitle(String title){
        return questionRepository.existsByTitle(title);
    }


    public void deleteQuestion(long questionId) {
        questionRepository.deleteById(questionId);
    }

    public void deleteQuestion(Question q) {
        deleteQuestion(q.getId());
    }
}