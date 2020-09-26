package es.unizar.murcy.repository;

import es.unizar.murcy.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findAnswersByDeletedIsFalse();

    Optional<Answer> findByIdAndDeletedIsFalse(long id);

    List<Answer> findByDeletedIsFalseAndQuiz_id(long id);

    Set<Answer> findAnswerByUser_IdAndDeletedIsFalse(long id);

}

