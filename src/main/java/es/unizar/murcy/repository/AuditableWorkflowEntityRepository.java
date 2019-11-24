package es.unizar.murcy.repository;

import es.unizar.murcy.model.AuditableWorkflowEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface AuditableWorkflowEntityRepository extends JpaRepository<AuditableWorkflowEntity, Long> {

}
