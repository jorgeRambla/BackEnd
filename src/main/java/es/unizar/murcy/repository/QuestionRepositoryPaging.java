package es.unizar.murcy.repository;

import es.unizar.murcy.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface QuestionRepositoryPaging extends PagingAndSortingRepository<Question, Long> {

    Page<Question> findQuestionsByOwner_idAndApprovedAndDeletedIsFalseAndTitleContainingIgnoreCase(long id,
                                                                       boolean approved, String query, Pageable pageable);
    Page<Question> findQuestionsByOwner_idAndDeletedIsFalseAndTitleContainingIgnoreCase(long id, String query, Pageable pageable);

    Page<Question> findQuestionsByOwner_idAndApprovedAndDeletedIsFalse(long id, boolean approved, Pageable pageable);
    Page<Question> findQuestionsByOwner_idAndDeletedIsFalse(long id, Pageable pageable);
}
