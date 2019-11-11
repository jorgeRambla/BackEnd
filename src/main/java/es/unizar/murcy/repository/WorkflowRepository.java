package es.unizar.murcy.repository;

import es.unizar.murcy.model.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkflowRepository extends JpaRepository<Workflow, Long> {
}
