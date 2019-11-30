package es.unizar.murcy.repository;

import es.unizar.murcy.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findQuizzesByDeletedIsFalse();

    Optional<Quiz> findByIdAndDeletedIsFalse(long id);

    Optional<Quiz> findByTitleAndDeletedIsFalse(String title);

    Boolean existsByTitleAndDeletedIsFalse(String title);

    List<Quiz> findByDeletedIsFalseAndUser_id(long id);

}
