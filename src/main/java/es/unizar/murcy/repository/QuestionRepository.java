package es.unizar.murcy.repository;

import es.unizar.murcy.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findQuestionById(long idQuestion);

    boolean existsByTitle(String title);

    Optional<Question> findQuestionByTitle(String title);

    List<Question> findQuestionsByUser_Id(long id);
}
