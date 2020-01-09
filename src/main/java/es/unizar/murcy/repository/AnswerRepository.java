package es.unizar.murcy.repository;

import es.unizar.murcy.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAnswersByDeletedIsFalse();

    Optional<Answer> findByIdAndDeletedIsFalse(long id);

    Optional<Answer> findByTitleAndDeletedIsFalse(String title);

    Boolean existsByTitleAndDeletedIsFalse(String title);

    List<Answer> findByDeletedIsFalseAndQuiz_id(long id);

}

