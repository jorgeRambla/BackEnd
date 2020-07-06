package es.unizar.murcy.repository;

import es.unizar.murcy.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuizRepositoryPaging extends PagingAndSortingRepository<Quiz, Long> {

    List<Quiz> findQuizzesByApprovedIsTrueAndClosedIsTrueAndDeletedIsFalseAndTitleContainingIgnoreCaseOrApprovedIsTrueAndClosedIsTrueAndDeletedIsFalseAndDescriptionIgnoreCaseContaining(String titleQuery, String descriptionQuery, Pageable pageable);

    Page<Quiz> findQuizzesByOwner_idAndApprovedAndDeletedIsFalseAndTitleContainingIgnoreCase(long id,
                                                                                             boolean approved, String query, Pageable pageable);
    Page<Quiz> findQuizzesByOwner_idAndDeletedIsFalseAndTitleContainingIgnoreCase(long id, String query, Pageable pageable);

    Page<Quiz> findQuizzesByOwner_idAndApprovedAndDeletedIsFalse(long id, boolean approved, Pageable pageable);
    Page<Quiz> findQuizzesByOwner_idAndDeletedIsFalse(long id, Pageable pageable);
}
