package es.unizar.murcy.controllers;

import es.unizar.murcy.controllers.utilities.AuthUtilities;
import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.model.dto.ErrorMessageDto;
import es.unizar.murcy.model.request.IndividualAnswerRequest;
import es.unizar.murcy.service.IndividualAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

public class IndividualAnswerController {

    @Autowired
    private AuthUtilities authUtilities;

    @Autowired
    private IndividualAnswerService individualAnswerService;

    @CrossOrigin
    @PostMapping("/api/individual_answer")
    public ResponseEntity create(@RequestBody IndividualAnswerRequest individualAnswerRequest) {

        if (individualAnswerRequest.isCreateValid().equals(Boolean.FALSE)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorMessageDto(HttpStatus.BAD_REQUEST));
        }

        IndividualAnswer individualAnswer = individualAnswerRequest.toEntity();
        individualAnswerService.create(individualAnswer);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
