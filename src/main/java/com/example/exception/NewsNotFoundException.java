package com.example.exception;

import lombok.Getter;

@Getter
public class NewsNotFoundException extends RuntimeException {

    private Long id;

    public NewsNotFoundException() {
        this("News was not found");
    }

    public NewsNotFoundException(Long id) {
        this("News was not found by id '" + id + "'", id);
    }

    public NewsNotFoundException(String message) {
        super(message);
    }

    public NewsNotFoundException(String message, Long id) {
        super(message);
        this.id = id;
    }
}
