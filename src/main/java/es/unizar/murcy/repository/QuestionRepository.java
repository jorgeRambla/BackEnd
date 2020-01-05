package es.unizar.murcy.repository;

import es.unizar.murcy.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Optional<Question> findByIdAndDeletedIsFalse(long idQuestion);

    boolean existsByTitleAndDeletedIsFalse(String title);

    Optional<Question> findQuestionByTitleAndDeletedIsFalse(String title);

    List<Question> findQuestionsByUser_IdAndDeletedIsFalse(long id);

    List<Question> findQuestionsByDeletedIsFalse();

    Set<Question> findQuestionsByClosedAndAndApprovedOrderByCreateDateDesc(boolean closed, boolean approved);

}
