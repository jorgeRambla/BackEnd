package es.unizar.murcy.model;


import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "murcy_option")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Option extends AuditableEntity {

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Boolean correct;

    public Option() {
        super();
    }
}
