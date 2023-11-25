package com.example.controller.advice;

import com.example.exception.NewsNotFoundException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NewsNotFoundControllerAdvice {

    @ExceptionHandler(NewsNotFoundException.class)
    public String handleNewsNotFoundException(NewsNotFoundException e, Model model) {
        model.addAttribute("message", e.getMessage());
        return "news/error";
    }
}
