package es.unizar.murcy.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "murcy_option")
@Data
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class Option extends AuditableEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

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
