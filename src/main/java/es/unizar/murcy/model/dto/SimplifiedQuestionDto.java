package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Question;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedQuestionDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private List<OptionDto> options;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private Boolean isMultiple;

    public SimplifiedQuestionDto(Question question) {
        this.id = question.getId();
        this.title = question.getTitle();
        this.description = question.getDescription();
        this.isMultiple = question.getIsMultiple();
        this.options = question.getOptions().stream().map(OptionDto::new).collect(Collectors.toList());
    }
}
