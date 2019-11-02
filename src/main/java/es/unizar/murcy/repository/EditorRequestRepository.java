package es.unizar.murcy.repository;

import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EditorRequestRepository extends JpaRepository<EditorRequest, Long> {
}
