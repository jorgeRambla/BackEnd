package es.unizar.murcy.model.request;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
public class UpdateWorkflowStatusRequest {
    @Getter
    @Setter
    private String response;

    public boolean isValid() {
        return response != null;
    }
}
