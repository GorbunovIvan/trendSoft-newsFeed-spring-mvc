package com.example.controller.converter;

import com.example.model.Category;
import com.example.service.CategoryService;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryConverter implements Converter<String, Category> {

    private final CategoryService categoryService;

    @Override
    public Category convert(@Nonnull String source) {
        if (source.isBlank() || source.equals("all categories")) {
            return null;
        }
        return categoryService.getByName(source);
    }
}
