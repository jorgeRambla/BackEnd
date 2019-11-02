package es.unizar.murcy.service;

import es.unizar.murcy.model.Workflow;
import es.unizar.murcy.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    WorkflowRepository workflowRepository;

    public Workflow create(Workflow workflow) {
        return  workflowRepository.save(workflow);
    }

    public Workflow update(Workflow workflow) {
        workflow.setModifiedDate(new Date());
        return workflowRepository.save(workflow);
    }
}
