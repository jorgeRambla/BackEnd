package es.unizar.murcy.model.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EditorRequestRequest {

    @Getter
    @Setter
    private String description;
}
