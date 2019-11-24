package es.unizar.murcy.model.extendable.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
public class AuditableEntity extends RemovableEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        super();
        Date date = new Date();
        this.createDate = date;
        this.modifiedDate = date;
    }
}