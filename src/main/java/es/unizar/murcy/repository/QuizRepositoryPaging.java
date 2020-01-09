package es.unizar.murcy.repository;

import es.unizar.murcy.model.Quiz;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuizRepositoryPaging extends PagingAndSortingRepository<Quiz, Long> {

    List<Quiz> findQuizzesByDeletedIsFalseAndTitleContainingIgnoreCaseOrDeletedIsFalseAndDescriptionIgnoreCaseContaining(String titleQuery, String descriptionQuery, Pageable pageable);
}
