package com.example.movieofficial.utils.dtos;

import lombok.Getter;
import lombok.Setter;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> extends RepresentationModel<PageResponse<T>> {
    private Long size;
    private Long totalPages;
    private List<T> data;

    private PageResponse(List<T> data, long totalPages) {
        this.data = data;
        this.totalPages = totalPages;
        this.size = (long) data.size();
    }

    public static <T> PageResponse.builder<T> builder() {
        return new PageResponse.builder<>();
    }

    public static class builder<T> {
        private List<T> data;
        private long totalPages;

        public PageResponse.builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public PageResponse.builder<T> totalPages(long totalPages) {
            this.totalPages = totalPages;
            return this;
        }

        public PageResponse<T> build() {
            return new PageResponse<>(data, totalPages);
        }
    }
}
