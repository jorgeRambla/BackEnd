package es.unizar.murcy.service;

import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class QuestionService {

    @Autowired
    QuestionRepository questionRepository;

    public List<Question> findAll() {
        return questionRepository.findAll();
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findById(id);
    }

    public Optional<Question> findByTitle(String title) {
        return questionRepository.findQuestionByTitle(title);
    }

    public Question update(Question question) {
        question.setModifiedDate(new Date());
        return questionRepository.save(question);
    }

    public Question create(Question question) {

        return questionRepository.save(question);
    }

    public boolean existsByTitle(String title) {
        return questionRepository.existsByTitle(title);
    }

    public void deleteById(long questionId) {
        questionRepository.deleteById(questionId);
    }

    public void delete(Question question) {
        questionRepository.delete(question);
    }

    public List<Question> findQuestionsByOwner(User user) {
        return findQuestionsByOwnerId(user.getId());
    }

    public List<Question> findQuestionsByOwnerId(long userId) {
        return questionRepository.findQuestionsByUser_Id(userId);
    }
}