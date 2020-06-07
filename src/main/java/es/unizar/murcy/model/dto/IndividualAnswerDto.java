package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.IndividualAnswer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
public class IndividualAnswerDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private long userId;

    @Getter
    @Setter
    private long questionId;

    @Getter
    @Setter
    private long timeInMillis;

    @Getter
    @Setter
    private List<OptionDto> options;

    public IndividualAnswerDto(IndividualAnswer individualAnswer) {
        this.id = individualAnswer.getId();
        this.userId = individualAnswer.getUser().getId();
        this.timeInMillis = individualAnswer.getTimeInMillis();
        this.questionId = individualAnswer.getQuestion().getId();

        this.options = individualAnswer.getOptions()
                .stream()
                .map(OptionDto::new)
                .collect(Collectors.toList());
    }

}
