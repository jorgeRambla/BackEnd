package es.unizar.murcy.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

    @Getter
    @Setter
    private Date createDate;

    @Getter
    @Setter
    private Date modifiedDate;

    public AuditableEntity() {
        Date date = new Date();
        this.createDate = date;
        this.modifiedDate = date;
    }
}