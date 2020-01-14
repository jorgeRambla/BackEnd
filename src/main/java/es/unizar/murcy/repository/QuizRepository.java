package es.unizar.murcy.repository;

import es.unizar.murcy.model.Quiz;
import es.unizar.murcy.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findQuizzesByDeletedIsFalse();

    Optional<Quiz> findByIdAndDeletedIsFalse(long id);

    Optional<Quiz> findByTitleAndDeletedIsFalse(String title);

    Boolean existsByTitleAndDeletedIsFalse(String title);

    List<Quiz> findByDeletedIsFalseAndUser_id(long id);

    Set<Quiz> findQuizByDeletedIsFalseAndClosedAndApprovedAndWorkflow_StatusInOrderByCreateDateDesc(boolean closed, boolean approved, Collection<Workflow.Status> validStatus);

    Optional<Quiz> findQuizByIdAndDeletedIsFalseAndClosedIsTrueAndApprovedIsTrue(long id);

}
