package es.unizar.murcy.model;


import es.unizar.murcy.model.extendable.jpa.AuditableEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "murcy_option")
@AllArgsConstructor
@ToString(callSuper = true)
public class Option extends AuditableEntity {

    protected static final long MAX_DELETION_DATE_DAYS = 1L;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private Boolean correct;

    public Option() {
        super();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode());
    }

    @Override
    public void delete() {
        setDeleted(true);
        setDeletionDate(new Date());
        setForceDeleteDate(new Date(System.currentTimeMillis() + (24L * 3600L * 1000L) * MAX_DELETION_DATE_DAYS));
    }
}
