package es.unizar.murcy.model;


import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "murcy_option")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private long id;

    @Getter
    @Setter
    private String text;

    @Getter
    @Setter
    private Boolean correct;
}
