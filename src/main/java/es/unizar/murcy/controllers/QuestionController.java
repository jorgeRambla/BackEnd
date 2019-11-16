package es.unizar.murcy.controllers;

import es.unizar.murcy.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
public class QuestionController {

    @Autowired
    QuestionService questionService;
}
