package es.unizar.murcy.repository;

import es.unizar.murcy.model.Option;
import es.unizar.murcy.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OptionRepository extends JpaRepository<Option, Long> {
    List<Option> findQuestionsByDeletedIsFalseAndIdIn(Collection<Long> ids);

}
