package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Option;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@SuppressWarnings("Duplicates")
public class SimplifiedOptionDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String title;

    public SimplifiedOptionDto(Option option) {
        this.id = option.getId();
        this.title = option.getTitle();
    }
}
