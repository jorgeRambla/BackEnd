package es.unizar.murcy.service;

import es.unizar.murcy.model.IndividualAnswer;
import es.unizar.murcy.repository.IndividualAnswerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class IndividualAnswerService {

    @Autowired
    IndividualAnswerRepository individualAnswerRepository;

    public List<IndividualAnswer> findAll() {
        return individualAnswerRepository.findIndividualAnswersByDeletedIsFalse();
    }

    public Optional<IndividualAnswer> findById(long id) {
        return individualAnswerRepository.findByIdAndDeletedIsFalse(id);
    }

    public IndividualAnswer update(IndividualAnswer answer) {
        answer.setModifiedDate(new Date());
        return individualAnswerRepository.save(answer);
    }

    public IndividualAnswer create(IndividualAnswer answer) {
        return individualAnswerRepository.save(answer);
    }

    public void delete(IndividualAnswer individualAnswer) {
        individualAnswer.delete();
        update(individualAnswer);
    }

    public List<IndividualAnswer> findIndividualAnswersByQuestionId(long questionId) {
        return individualAnswerRepository.findByDeletedIsFalseAndQuestion_id(questionId);
    }

    public List<IndividualAnswer> findIndividualAnswersByAnswerId(long answerId) {
        return individualAnswerRepository.findByDeletedIsFalseAndAnswer_id(answerId);
    }
}