package es.unizar.murcy.repository;

import es.unizar.murcy.model.HelloWorld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HelloWorldRepository extends JpaRepository<HelloWorld, Long>  {
    Optional<HelloWorld> findById(long id);

}