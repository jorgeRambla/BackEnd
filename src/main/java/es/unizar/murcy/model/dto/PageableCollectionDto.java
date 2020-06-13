package es.unizar.murcy.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;

@NoArgsConstructor
@AllArgsConstructor
public class PageableCollectionDto<T> {

    @Getter
    @Setter
    public Collection<T> data;

    @Getter
    @Setter
    public long length;
}
