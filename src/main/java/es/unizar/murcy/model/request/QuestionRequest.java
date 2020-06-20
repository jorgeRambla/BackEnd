package es.unizar.murcy.model.request;

import es.unizar.murcy.model.Question;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@SuppressWarnings("Duplicates")
public class QuestionRequest {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private List<OptionRequest> options;

    @Getter
    @Setter
    private Boolean publish;

    public boolean isValid() {
        return title != null
                && !title.equals("")
                && options != null
                && (options.size() >= Question.MIN_OPTIONS && options.size() <= Question.MAX_OPTIONS);
    }

    public boolean isUpdateValid() {
        return options == null || options.isEmpty() || (options.size() >= Question.MIN_OPTIONS && options.size() <= Question.MAX_OPTIONS);
    }
    
    public Question toEntity() {
        Question question = new Question();
        question.setTitle(title);
        question.setDescription((this.description == null) ? "" : description);
        question.setOptions(options.stream().map(OptionRequest::toEntity).collect(Collectors.toList()));
        question.setIsMultiple(isMultiple());
        return question;
    }

    public Boolean isMultiple() {
        if(options == null) {
            return false;
        }
        long corrects = options.stream().filter(OptionRequest::isCorrect).count();
        return corrects > 1 || corrects == 0;
    }
}
