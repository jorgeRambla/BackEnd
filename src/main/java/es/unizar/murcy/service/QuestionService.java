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

    public Question update(Question question) {
        question.setModifiedDate(new Date());
        return questionRepository.save(question);
    }

    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }

    public boolean existsByTitle(String title){
        return questionRepository.existsByTitle(title);
    }


    public void deleteQuestionById(long questionId) {
        questionRepository.deleteById(questionId);
    }

    public void deleteQuestion(Question question) {
        questionRepository.delete(question);
    }
}