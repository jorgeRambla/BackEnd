package es.unizar.murcy.model.extendable.jpa;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@MappedSuperclass
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AuditableEntity{

    protected static final int MAX_DELETION_DAYS = 4;
    protected static final long MAX_DELETION_MILLIS = MAX_DELETION_DAYS * 24L * 3600L * 1000L;

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

    @Getter
    @Setter
    private boolean deleted;

    @Getter
    @Setter
    private Date deletionDate;

    @Getter
    @Setter
    private Date forceDeleteDate;

    public AuditableEntity() {
        super();
        Date date = new Date();
        this.createDate = date;
        this.modifiedDate = date;
        this.deleted = false;
    }

    public void delete() {
        this.deleted = true;
        this.deletionDate = new Date();
        this.forceDeleteDate = new Date(System.currentTimeMillis() + MAX_DELETION_MILLIS);
    }
}