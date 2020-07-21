package es.unizar.murcy.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import es.unizar.murcy.model.Quiz;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
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

    @Getter
    @Setter
    private Date createdDate;

    @Getter
    @Setter
    private Date lastModifiedDate;

    @Getter
    @Setter
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<SimplifiedQuestionDto> questions;

    public SimplifiedQuizDto(Quiz quiz){
        this.id = quiz.getId();
        this.title = quiz.getTitle();
        this.description = quiz.getDescription();
        this.ownerUserName = quiz.getOwner().getUsername();
        this.questions = quiz.getQuestions().stream().map(SimplifiedQuestionDto::new).collect(Collectors.toList());
        this.createdDate = quiz.getCreateDate();
        this.lastModifiedDate = quiz.getModifiedDate();

    }

    public static SimplifiedQuizDto buildWithOutQuestions(Quiz quiz) {
        SimplifiedQuizDto simplifiedQuizDto = new SimplifiedQuizDto();
        simplifiedQuizDto.id = quiz.getId();
        simplifiedQuizDto.title = quiz.getTitle();
        simplifiedQuizDto.description = quiz.getDescription();
        simplifiedQuizDto.ownerUserName = quiz.getOwner().getUsername();
        simplifiedQuizDto.questions = null;
        simplifiedQuizDto.createdDate = quiz.getCreateDate();
        simplifiedQuizDto.lastModifiedDate = quiz.getModifiedDate();

        return simplifiedQuizDto;
    }
}
