package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.Question;
import es.unizar.murcy.model.User;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.dto.IndividualAnswerDto;
import es.unizar.murcy.model.request.IndividualAnswerRequest;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.IndividualAnswerService;
import es.unizar.murcy.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Optional;

public class IndividualAnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private IndividualAnswerService individualAnswerService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private QuestionService questionService;

    //TODO: controller for getting all individual answers for one question GET /api/question/{id}/anwsers -> Lista de respuestas individuales
}
