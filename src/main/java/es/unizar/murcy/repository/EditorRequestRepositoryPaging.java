package es.unizar.murcy.repository;

import es.unizar.murcy.model.EditorRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;


public interface EditorRequestRepositoryPaging extends PagingAndSortingRepository<EditorRequest, Long> {

    Page<EditorRequest> findEditorRequestsByClosedAndAndApprovedAndDeletedIsFalse(Boolean closed, Boolean approved, Pageable pageable);

    Page<EditorRequest> findEditorRequestsByDeletedIsFalse(Pageable pageable);

}
