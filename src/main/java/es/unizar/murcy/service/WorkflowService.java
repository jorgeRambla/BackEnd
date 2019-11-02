package es.unizar.murcy.service;

import es.unizar.murcy.repository.WorkflowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class WorkflowService {

    @Autowired
    WorkflowRepository workflowRepository;
}
