package es.unizar.murcy.model.request;

public class EditorRequestRequest {
    private String description;

    public EditorRequestRequest() {
    }

    public EditorRequestRequest(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
