package com.beninexplo.backend.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponseDTO<T> {

    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;

    public static <T> PageResponseDTO<T> from(Page<T> page) {
        PageResponseDTO<T> dto = new PageResponseDTO<>();
        dto.content = page.getContent();
        dto.page = page.getNumber();
        dto.size = page.getSize();
        dto.totalElements = page.getTotalElements();
        dto.totalPages = page.getTotalPages();
        return dto;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }
}
