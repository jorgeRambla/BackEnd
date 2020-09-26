package es.unizar.murcy.service;

import es.unizar.murcy.model.EditorRequest;
import es.unizar.murcy.model.User;
import es.unizar.murcy.repository.EditorRequestRepository;
import es.unizar.murcy.repository.EditorRequestRepositoryPaging;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.Optional;

import static es.unizar.murcy.service.utilities.SortUtilities.buildPageRequest;
import static es.unizar.murcy.service.utilities.SortUtilities.buildSort;

@Service
@Transactional
public class EditorRequestService {

    private final EditorRequestRepository editorRequestRepository;
    private final EditorRequestRepositoryPaging editorRequestRepositoryPaging;

    public EditorRequestService(EditorRequestRepository editorRequestRepository,
                                EditorRequestRepositoryPaging editorRequestRepositoryPaging) {
        this.editorRequestRepository = editorRequestRepository;
        this.editorRequestRepositoryPaging = editorRequestRepositoryPaging;
    }

    public Optional<EditorRequest> findEditorRequestByApplicant(User user) {
        return editorRequestRepository.findEditorRequestByOwner(user);
    }

    public EditorRequest create(EditorRequest editorRequest) {
        return editorRequestRepository.save(editorRequest);
    }

    public EditorRequest update(EditorRequest editorRequest) {
        editorRequest.setModifiedDate(new Date());
        return editorRequestRepository.save(editorRequest);
    }

    public Page<EditorRequest> findByClosedAndApproved(Boolean isClosed, Boolean isApproved) {
        return this.findByClosedAndApproved(false, isClosed, isApproved, -1, 0, "createDate", "desc");
    }

    public Page<EditorRequest> findByClosedAndApproved(Boolean all, Boolean isClosed, Boolean isApproved, int page,
                                                       int size, String sortColumn, String sortType) {
        Sort sort = buildSort(sortType, sortColumn);
        PageRequest pageRequest = buildPageRequest(page, size, sort);
        Page<EditorRequest> editorRequests;

        if(all.equals(Boolean.FALSE)) {
            editorRequests = editorRequestRepositoryPaging.findEditorRequestsByClosedAndAndApprovedAndDeletedIsFalse(isClosed, isApproved, pageRequest);
        } else {
            editorRequests = editorRequestRepositoryPaging.findEditorRequestsByDeletedIsFalse(pageRequest);
        }

        return editorRequests;
    }
}
