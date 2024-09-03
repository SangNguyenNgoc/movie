package com.example.movieofficial.utils.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ListResponse<T> {

    private Long size;
    private List<T> data;

    private ListResponse(List<T> data) {
        this.data = data;
        this.size = (long) data.size();
    }

    public static <T> builder<T> builder() {
        return new builder<>();
    }

    public static class builder<T> {
        private List<T> data;

        public builder<T> data(List<T> data) {
            this.data = data;
            return this;
        }

        public ListResponse<T> build() {
            return new ListResponse<>(data);
        }
    }
}
