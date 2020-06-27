package es.unizar.murcy.service;

import es.unizar.murcy.model.*;
import es.unizar.murcy.repository.OptionRepository;
import es.unizar.murcy.repository.QuestionRepository;
import es.unizar.murcy.repository.QuestionRepositoryPaging;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

import static es.unizar.murcy.service.utilities.SortUtilities.buildSort;
import static es.unizar.murcy.service.utilities.SortUtilities.buildPageRequest;

@Service
@Transactional
public class QuestionService {

    @Value("${murcy.config.entity.hard-delete}")
    private Boolean hardDelete;

    QuestionRepository questionRepository;
    QuestionRepositoryPaging questionRepositoryPaging;
    OptionRepository optionRepository;
    WorkflowService workflowService;

    public QuestionService(QuestionRepository questionRepository, OptionRepository optionRepository,
                           WorkflowService workflowService, QuestionRepositoryPaging questionRepositoryPaging) {
        this.questionRepository = questionRepository;
        this.optionRepository = optionRepository;
        this.workflowService = workflowService;
        this.questionRepositoryPaging = questionRepositoryPaging;
    }

    public List<Question> findAll() {
        return questionRepository.findQuestionsByDeletedIsFalse();
    }

    public Optional<Question> findById(long id) {
        return questionRepository.findByIdAndDeletedIsFalse(id);
    }

    public Optional<Question> findByTitle(String title) {
        return questionRepository.findQuestionByTitleAndDeletedIsFalse(title);
    }

    public Question update(Question question) {
        question.getOptions().forEach(option -> option.setCreateDate(question.getCreateDate()));
        question.setOptions(optionRepository.saveAll(question.getOptions()));
        question.setModifiedDate(new Date());
        return questionRepository.save(question);
    }

    public Question create(Question question) {
        question.setOptions(optionRepository.saveAll(question.getOptions()));
        return questionRepository.save(question);
    }

    public boolean existsByTitle(String title) {
        return questionRepository.existsByTitleAndDeletedIsFalse(title);
    }


    public void delete(Question question) {
        delete(question, hardDelete);
    }

    protected void delete(Question question, Boolean hardDelete) {
        if(hardDelete.equals(Boolean.TRUE)) {
            question.setOptions(Collections.emptyList());
            questionRepository.delete(question);
        } else {
            deleteOptions(question.getOptions(), false);
            question.delete(); // Mark question as deleted
            update(question);
        }
    }

    public void deleteOptions(Question question) {
        List<Option> options = new ArrayList<>(question.getOptions());
        if(hardDelete.equals(Boolean.TRUE)){
            question.getOptions().clear();
            questionRepository.save(question);
            deleteOptions(options, hardDelete);
        } else {
            deleteOptions(question.getOptions(), false);
        }
    }

    protected void deleteOptions(List<Option> options, Boolean hardDelete) {
        if(hardDelete.equals(Boolean.TRUE)) {
            options.forEach(optionRepository::delete);
        } else {
            options.forEach(Option::delete);
            optionRepository.saveAll(options);
        }
    }

    public List<Question> findQuestionsByOwner(User user) {
        return findQuestionsByOwnerId(user.getId());
    }

    public List<Question> findQuestionsByOwnerId(long userId) {
        return questionRepository.findQuestionsByOwner_IdAndDeletedIsFalse(userId);
    }

    public Page<Question> findQuestionsByOwnerId(Boolean all, Boolean publish, User user, int page,
                                                 int size, String sortColumn, String sortType, String query) {
        Sort sort = buildSort(sortType, sortColumn);
        PageRequest pageRequest = buildPageRequest(page, size, sort);
        Page<Question> questions;

        if(all.equals(Boolean.FALSE)) {
            if (query.isEmpty()) {
                questions = questionRepositoryPaging.findQuestionsByOwner_idAndApprovedAndDeletedIsFalse(user.getId(), publish, pageRequest);
            } else {
                questions = questionRepositoryPaging.findQuestionsByOwner_idAndApprovedAndDeletedIsFalseAndTitleContainingIgnoreCase(user.getId(), publish, query, pageRequest);
            }
        } else {
            if (query.isEmpty()) {
                questions = questionRepositoryPaging.findQuestionsByOwner_idAndDeletedIsFalse(user.getId(), pageRequest);
            } else {
                questions = questionRepositoryPaging.findQuestionsByOwner_idAndDeletedIsFalseAndTitleContainingIgnoreCase(user.getId(), query, pageRequest);
            }
        }
        return questions;
    }

    public Set<Question> findByClosedAndApproved(boolean closed, boolean approved) {
        Set<Workflow.Status> validStatus = new HashSet<>();
        validStatus.add(Workflow.Status.PENDING);
        validStatus.add(Workflow.Status.APPROVED);
        validStatus.add(Workflow.Status.DENIED);

        return questionRepository.findQuestionsByDeletedIsFalseAndClosedAndApprovedAndLastWorkflow_StatusInOrderByCreateDateDesc(closed, approved, validStatus);
    }
}