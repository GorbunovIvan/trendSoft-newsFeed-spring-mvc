package com.example.service;

import com.example.model.Category;
import com.example.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class CategoryServiceTest {

    @Autowired
    private CategoryService categoryService;

    @MockBean
    private CategoryRepository categoryRepository;

    private List<Category> categoriesInDB;

    @BeforeEach
    void setUp() {

        categoriesInDB = List.of(
                new Category(1, "category 1", new ArrayList<>()),
                new Category(2, "category 2", new ArrayList<>())
        );

        Mockito.reset(categoryRepository);

        when(categoryRepository.findAll()).thenReturn(new ArrayList<>(categoriesInDB));
        when(categoryRepository.findById(-1)).thenReturn(Optional.empty());
        when(categoryRepository.findByName("")).thenReturn(Optional.empty());

        for (var news : categoriesInDB) {
            when(categoryRepository.findById(news.getId())).thenReturn(Optional.of(news));
            when(categoryRepository.findByName(news.getName())).thenReturn(Optional.of(news));
        }
    }

    @Test
    void testGetAll() {
        assertEquals(categoriesInDB, categoryService.getAll());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void testGetById() {
        for (var news : categoriesInDB) {
            assertEquals(news, categoryService.getById(news.getId()));
            verify(categoryRepository, times(1)).findById(news.getId());
        }
    }

    @Test
    void testGetByIdNotFound() {
        assertNull(categoryService.getById(-1));
        verify(categoryRepository, times(1)).findById(-1);
    }

    @Test
    void testGetByName() {
        for (var news : categoriesInDB) {
            assertEquals(news, categoryService.getByName(news.getName()));
            verify(categoryRepository, times(1)).findByName(news.getName());
        }
    }

    @Test
    void testGetByNameNotFound() {
        assertNull(categoryService.getByName(""));
        verify(categoryRepository, times(1)).findByName("");
    }
}