package es.unizar.murcy.repository;

import es.unizar.murcy.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QuizRepository extends JpaRepository<Quiz, Long> {

}
