package es.unizar.murcy.repository;

import es.unizar.murcy.model.Quiz;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


public interface QuizRepositoryPaging extends PagingAndSortingRepository<Quiz, Long> {

    List<Quiz> findQuizzesByDeletedIsFalseAndTitleContainingOrDeletedIsFalseAndDescriptionContaining(String query, Pageable pageable);
}
