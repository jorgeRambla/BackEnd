package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Option;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class OptionRequest {
    @Getter
    @Setter
    private String title;
    @Getter
    @Setter
    private boolean correct;

    public Option toEntity() {
        Option option = new Option();
        option.setTitle(title);
        option.setCorrect(correct);
        return option;
    }
}
