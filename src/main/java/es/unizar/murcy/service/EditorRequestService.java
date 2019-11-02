package es.unizar.murcy.service;

import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.EditorRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

@Service
@Transactional
public class EditorRequestService {

    @Autowired
    WorkflowService workflowService;

    @Autowired
    EditorRequestRepository editorRequestRepository;

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
}
