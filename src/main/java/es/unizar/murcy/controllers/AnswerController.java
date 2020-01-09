package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.Answer;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.request.AnswerRequest;
import es.unizar.murcy.service.AnswerService;
import es.unizar.murcy.service.IndividualAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

public class AnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private IndividualAnswerService individualAnswerService;

    @Autowired
    private AnswerService answerService;

    @CrossOrigin
    @PostMapping("/api/answer")
    public ResponseEntity create(@RequestBody AnswerRequest answerRequest) {

        if (answerRequest.isCreateValid().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        if (answerService.existsByTitle(answerRequest.getTitle())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST, "Ya existe un usuario con ese nombre"));
        }

        Answer answer = answerRequest.toEntity(individualAnswerService);
        answerService.create(answer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @CrossOrigin
    @GetMapping(value = "/api/answer/{id}")
    public ResponseEntity fetchAnswerById(@PathVariable long id) {
        Optional<Answer> optionalAnswer = answerService.findById(id);

        if (!optionalAnswer.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessageDto(HttpStatus.NOT_FOUND));
        }

        return ResponseEntity.status(HttpStatus.OK).body(optionalAnswer.get());
    }

}
