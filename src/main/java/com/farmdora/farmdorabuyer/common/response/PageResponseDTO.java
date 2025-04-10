package com.farmdora.farmdorabuyer.common.response;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResponseDTO<T> {
    private int currentPage;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrev;
    private int pageSize;
    private List<T> contents;

    public PageResponseDTO (Page page, List<T> contents) {
        this.currentPage = page.getNumber();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.hasNext = page.hasNext();
        this.hasPrev = page.hasPrevious();
        this.pageSize = page.getSize();
        this.contents = contents;
    }
}
