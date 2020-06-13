package es.unizar.murcy.service;

import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.EditorRequestRepository;
import es.unizar.murcy.repository.EditorRequestRepositoryPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

@Service
@Transactional
public class EditorRequestService {

    private final WorkflowService workflowService;
    private final EditorRequestRepository editorRequestRepository;
    private final EditorRequestRepositoryPaging editorRequestRepositoryPaging;

    public EditorRequestService(WorkflowService workflowService, EditorRequestRepository editorRequestRepository,
                                EditorRequestRepositoryPaging editorRequestRepositoryPaging) {
        this.workflowService = workflowService;
        this.editorRequestRepository = editorRequestRepository;
        this.editorRequestRepositoryPaging = editorRequestRepositoryPaging;
    }

    public Optional<EditorRequest> findEditorRequestByApplicant(User user) {
        return editorRequestRepository.findEditorRequestByApplicant(user);
    }

    public EditorRequest create(EditorRequest editorRequest) {
        return editorRequestRepository.save(editorRequest);
    }

    public EditorRequest update(EditorRequest editorRequest) {
        editorRequest.setModifiedDate(new Date());
        return editorRequestRepository.save(editorRequest);
    }

    public Collection<EditorRequest> findByClosedAndApproved(Boolean isClosed, Boolean isApproved) {
        return editorRequestRepository.findEditorRequestByClosedAndAndApprovedAndDeletedIsFalseOrderByCreateDateDesc(isClosed, isApproved);
    }

    public Page<EditorRequest> findByClosedAndApproved(Boolean isClosed, Boolean isApproved, Pageable pageable) {
        return editorRequestRepositoryPaging.findEditorRequestByClosedAndAndApprovedAndDeletedIsFalse(isClosed, isApproved, pageable);
    }
}
