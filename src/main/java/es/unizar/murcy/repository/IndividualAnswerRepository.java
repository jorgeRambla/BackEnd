package es.unizar.murcy.repository;

import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;


public interface IndividualAnswerRepository extends JpaRepository<IndividualAnswer, Long> {

    List<IndividualAnswer> findIndividualAnswersByDeletedIsFalse();

    Optional<IndividualAnswer> findByIdAndDeletedIsFalse(long id);

    List<IndividualAnswer> findByDeletedIsFalseAndQuestion_id(long id);

    List<IndividualAnswer> findByDeletedIsFalseAndAnswer_id(long id);

    List<IndividualAnswer> findByDeletedIsFalseAndIdIn(Collection<Long> ids);
}