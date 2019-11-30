package es.unizar.murcy.service;

import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.OptionRepository;
import es.unizar.murcy.repository.QuestionRepository;
import es.unizar.murcy.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    /*public List<Quiz> findAll() {
        return questionRepository.findQuestionsByDeletedIsFalse();
    }

    public Optional<Quiz> findById(long id) {
        return questionRepository.findByIdAndDeletedIsFalse(id);
    }

    public Optional<Quiz> findByTitle(String title) {
        return questionRepository.findQuestionByTitleAndDeletedIsFalse(title);
    }

    public Quiz update(Quiz quiz) {
        question.getOptions().forEach(option -> option.setCreateDate(question.getCreateDate()));
        optionRepository.saveAll(question.getOptions());
        question.setModifiedDate(new Date());
        return questionRepository.save(question);
    }

    public Quiz create(Question question) {
        question.setOptions(optionRepository.saveAll(question.getOptions()));
        return questionRepository.save(question);
    }

    public boolean existsByTitle(String title) {
        return questionRepository.existsByTitleAndDeletedIsFalse(title);
    }


    public void delete(Quiz quiz) {
        optionRepository.saveAll(question.getOptions());
        question.delete();
        update(question);
    }

    public void deleteOptions(List<Option> options) {
        options.forEach(Option::delete);
        optionRepository.saveAll(options);
    }

    public List<Quiz> findQuestionsByOwner(User user) {
        return findQuestionsByOwnerId(user.getId());
    }

    public List<Quiz> findQuestionsByOwnerId(long userId) {
        return questionRepository.findQuestionsByUser_IdAndDeletedIsFalse(userId);
    }*/
}