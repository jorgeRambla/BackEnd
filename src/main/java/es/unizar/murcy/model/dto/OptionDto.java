package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Option;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("Duplicates")
public class OptionDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private boolean correct;

    public OptionDto(Option option) {
        this.id = option.getId();
        this.title = option.getTitle();
        this.correct = option.getCorrect();
    }
}
