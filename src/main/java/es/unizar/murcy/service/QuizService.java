package es.unizar.murcy.service;

import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.QuizRepository;
import es.unizar.murcy.repository.QuizRepositoryPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizRepositoryPaging quizRepositoryPaging;

    public List<Quiz> findAll() {
        return quizRepository.findQuizzesByDeletedIsFalse();
    }

    public Optional<Quiz> findById(long id) {
        return quizRepository.findByIdAndDeletedIsFalse(id);
    }

    public Optional<Quiz> findByTitle(String title) {
        return quizRepository.findByTitleAndDeletedIsFalse(title);
    }

    public Quiz update(Quiz quiz) {
        quiz.setModifiedDate(new Date());
        return quizRepository.save(quiz);
    }

    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public boolean existsByTitle(String title) {
        return quizRepository.existsByTitleAndDeletedIsFalse(title);
    }


    public void delete(Quiz quiz) {
        quiz.delete();
        update(quiz);
    }

    public List<Quiz> findQuizzesByOwnerId(User user) {
        return findQuizzesByOwnerId(user.getId());
    }

    public List<Quiz> findQuizzesByOwnerId(long userId) {
        return quizRepository.findByDeletedIsFalseAndUser_id(userId);
    }

    public Set<Quiz> findByClosedAndApproved(boolean closed, boolean approved) {
        return quizRepository.findQuizzesByClosedAndAndApprovedOrderByCreateDateDesc(closed, approved);
    }

    public List<Quiz> searchQuizzes(String query, Pageable pageable) {
        return quizRepositoryPaging.findQuizzesByDeletedIsFalseAndTitleContainingOrDeletedIsFalseAndDescriptionContaining(query, pageable);
    }
}