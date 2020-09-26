package es.unizar.murcy.service.utilities;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

public class SortUtilities {

    private SortUtilities() {}

    public static Sort buildSort(String sortType, String sortColumn) {
        if(sortType.equalsIgnoreCase("asc")) {
            return Sort.by(sortColumn).ascending();
        } else {
            return Sort.by(sortColumn).descending();
        }
    }

    public static PageRequest buildPageRequest(int page, int size, Sort sort) {
        if(page == -1) {
            return PageRequest.of(0, Integer.MAX_VALUE, sort);
        } else {
            return PageRequest.of(page, size, sort);
        }
    }
}
