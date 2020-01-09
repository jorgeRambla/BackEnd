package es.unizar.murcy.model.dto;

import es.unizar.murcy.model.Quiz;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedQuizDto {

    @Getter
    @Setter
    private long id;

    @Getter
    @Setter
    private String ownerUserName;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    public SimplifiedQuizDto(Quiz quiz){
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.description = quiz.getDescription();
        this.ownerUserName = quiz.getUser().getUsername();
    }
}
