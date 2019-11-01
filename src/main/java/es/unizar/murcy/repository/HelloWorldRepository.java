package es.unizar.murcy.repository;

import es.unizar.murcy.model.HelloWorld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
public interface HelloWorldRepository extends JpaRepository<HelloWorld, Long>  {
    boolean existsByUsername(String username);

    HelloWorld findByUsername(String username);

    @Transactional
    void deleteByUsername(String username);

}