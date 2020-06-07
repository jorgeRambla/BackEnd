package es.unizar.murcy.repository;

import es.unizar.murcy.model.IndividualAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface IndividualAnswerRepository extends JpaRepository<IndividualAnswer, Long> {

    List<IndividualAnswer> findIndividualAnswersByDeletedIsFalse();

    Optional<IndividualAnswer> findByIdAndDeletedIsFalse(long id);

    List<IndividualAnswer> findByDeletedIsFalseAndQuestion_id(long id);

    List<IndividualAnswer> findByDeletedIsFalseAndAnswer_id(long id);

}