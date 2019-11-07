package es.unizar.murcy.repository;

import es.unizar.murcy.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findQuestionById(long idQuestion);

    Optional<Question> findQuestionByDescription(String description);

    boolean existsByTitle(String t);

    Optional<Question> findQuestionByTitle(String title);
}
