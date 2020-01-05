package es.unizar.murcy.service;

import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.OptionRepository;
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

    @Autowired
    OptionRepository optionRepository;

    public List<Question> findAll() {
        return questionRepository.findQuestionsByDeletedIsFalse();
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findByIdAndDeletedIsFalse(id);
    }

    public Optional<Question> findByTitle(String title) {
        return questionRepository.findQuestionByTitleAndDeletedIsFalse(title);
    }

    public Question update(Question question) {
        question.getOptions().forEach(option -> option.setCreateDate(question.getCreateDate()));
        question.setOptions(optionRepository.saveAll(question.getOptions()));
        question.setModifiedDate(new Date());
        return questionRepository.save(question);
    }

    public Question create(Question question) {
        question.setOptions(optionRepository.saveAll(question.getOptions()));
        return questionRepository.save(question);
    }

    public boolean existsByTitle(String title) {
        return questionRepository.existsByTitleAndDeletedIsFalse(title);
    }


    public void delete(Question question) {
        optionRepository.saveAll(question.getOptions());
        question.delete();
        update(question);
    }

    public void deleteOptions(List<Option> options, Boolean hardDelete) {
        if(hardDelete.equals(Boolean.TRUE)) {
            optionRepository.deleteInBatch(options);
        } else {
            options.forEach(Option::delete);
            optionRepository.saveAll(options);
        }
    }

    public List<Question> findQuestionsByOwner(User user) {
        return findQuestionsByOwnerId(user.getId());
    }

    public List<Question> findQuestionsByOwnerId(long userId) {
        return questionRepository.findQuestionsByUser_IdAndDeletedIsFalse(userId);
    }

    public Set<Question> findByClosedAndApproved(boolean closed, boolean approved) {
        return questionRepository.findQuestionsByClosedAndAndApprovedOrderByCreateDateDesc(closed, approved);
    }
}