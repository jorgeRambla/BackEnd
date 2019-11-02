package es.unizar.murcy.repository;

import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.Set;

public interface EditorRequestRepository extends JpaRepository<EditorRequest, Long> {

    Optional<EditorRequest> findEditorRequestByApplicant(User user);

    Set<EditorRequest> findEditorRequestByClosedIsFalse();

    Set<EditorRequest> findEditorRequestByClosedAndAndApprovedOrderByCreateDateDesc(Boolean closed, Boolean approved);
}
