package es.unizar.murcy.model.extendable.jpa;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.MappedSuperclass;
import java.util.Date;

@MappedSuperclass
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class RemovableEntity {
    @Getter
    @Setter
    private boolean deleted;

    @Getter
    @Setter
    private Date deletionDate;

    public RemovableEntity() {
        this.deleted = false;
    }

    public void delete() {
        this.deleted = true;
        this.deletionDate = new Date();
    }
}
