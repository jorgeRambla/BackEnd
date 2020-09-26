package es.unizar.murcy.service;

import es.unizar.murcy.model.*;
import es.unizar.murcy.repository.QuizRepository;
import es.unizar.murcy.repository.QuizRepositoryPaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static es.unizar.murcy.service.utilities.SortUtilities.buildPageRequest;
import static es.unizar.murcy.service.utilities.SortUtilities.buildSort;

@Service
@Transactional
public class QuizService {

    @Autowired
    QuizRepository quizRepository;

    @Autowired
    QuizRepositoryPaging quizRepositoryPaging;

    private Set<Workflow.Status> getWorkflowRequestsStatus() {
        Set<Workflow.Status> validStatus = new HashSet<>();
        validStatus.add(Workflow.Status.PENDING);
        validStatus.add(Workflow.Status.APPROVED);
        validStatus.add(Workflow.Status.DENIED);
        return validStatus;
    }

    public List<Quiz> findAll() {
        return quizRepository.findQuizzesByDeletedIsFalse();
    }

    public Optional<Quiz> findById(long id) {
        return quizRepository.findByIdAndDeletedIsFalse(id);
    }

    public Optional<Quiz> findByTitle(String title) {
        return quizRepository.findByTitleAndDeletedIsFalse(title);
    }

    public Quiz update(Quiz quiz) {
        quiz.setModifiedDate(new Date());
        return quizRepository.save(quiz);
    }

    public Quiz create(Quiz quiz) {
        return quizRepository.save(quiz);
    }

    public boolean existsByTitle(String title) {
        return quizRepository.existsByTitleAndDeletedIsFalse(title);
    }


    public void delete(Quiz quiz) {
        quiz.delete();
        update(quiz);
    }

    public List<Quiz> findQuizzesByOwnerId(long userId) {
        return quizRepository.findByDeletedIsFalseAndOwner_id(userId);
    }

    public Page<Quiz> findQuizzesByOwnerId(Boolean all, Boolean publish, User user, int page,
                                                 int size, String sortColumn, String sortType, String query) {
        Sort sort = buildSort(sortType, sortColumn);
        PageRequest pageRequest = buildPageRequest(page, size, sort);
        Page<Quiz> quizzes;

        if(all.equals(Boolean.FALSE)) {
            if (query.isEmpty()) {
                quizzes = quizRepositoryPaging.findQuizzesByOwner_idAndApprovedAndDeletedIsFalse(user.getId(), publish, pageRequest);
            } else {
                quizzes = quizRepositoryPaging.findQuizzesByOwner_idAndApprovedAndDeletedIsFalseAndTitleContainingIgnoreCase(user.getId(), publish, query, pageRequest);
            }
        } else {
            if (query.isEmpty()) {
                quizzes = quizRepositoryPaging.findQuizzesByOwner_idAndDeletedIsFalse(user.getId(), pageRequest);
            } else {
                quizzes = quizRepositoryPaging.findQuizzesByOwner_idAndDeletedIsFalseAndTitleContainingIgnoreCase(user.getId(), query, pageRequest);
            }
        }
        return quizzes;
    }

    public Set<Quiz> findByClosedAndApproved(boolean closed, boolean approved) {
        return quizRepository.findQuizByDeletedIsFalseAndClosedAndApprovedAndLastWorkflow_StatusInOrderByCreateDateDesc(closed, approved, getWorkflowRequestsStatus());
    }

    public Page<Quiz> findByClosedAndApproved(Boolean all, Boolean isClosed, Boolean isApproved, int page,
                                             int size, String sortColumn, String sortType) {
        Sort sort = buildSort(sortType, sortColumn);
        PageRequest pageRequest = buildPageRequest(page, size, sort);
        Page<Quiz> quizzes;

        if(all.equals(Boolean.FALSE)) {
            quizzes = quizRepositoryPaging.findQuizzesByClosedAndAndApprovedAndDeletedIsFalseAndLastWorkflow_StatusIn(isClosed, isApproved, pageRequest, getWorkflowRequestsStatus());
        } else {
            quizzes = quizRepositoryPaging.findQuizzesByDeletedIsFalseAndLastWorkflow_StatusIn(pageRequest, getWorkflowRequestsStatus());
        }

        return quizzes;
    }

    public Optional<Quiz> findByPublishAndId(long id) {
        return quizRepository.findQuizByIdAndDeletedIsFalseAndClosedIsTrueAndApprovedIsTrue(id);
    }

    public Page<Quiz> searchQuizzes(String query, int page, int size, String sortColumn, String sortType) {
        Sort sort = buildSort(sortType, sortColumn);
        PageRequest pageRequest = buildPageRequest(page, size, sort);
        return quizRepositoryPaging.findQuizzesByApprovedIsTrueAndClosedIsTrueAndDeletedIsFalseAndTitleContainingIgnoreCaseOrApprovedIsTrueAndClosedIsTrueAndDeletedIsFalseAndDescriptionIgnoreCaseContaining(query, query, pageRequest);
    }
}