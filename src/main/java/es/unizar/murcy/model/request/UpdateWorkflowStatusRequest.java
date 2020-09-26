package es.unizar.murcy.model.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@SuppressWarnings("Duplicates")
public class UpdateWorkflowStatusRequest {
    @Getter
    @Setter
    private String response;

    public boolean isValid() {
        return response != null;
    }
}
