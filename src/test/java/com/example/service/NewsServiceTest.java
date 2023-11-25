package com.example.service;

import com.example.exception.NewsNotFoundException;
import com.example.model.Category;
import com.example.model.News;
import com.example.repository.NewsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class NewsServiceTest {

    @Autowired
    private NewsService newsService;
    
    @MockBean
    private NewsRepository newsRepository;

    private List<News> newsListInDB;

    private News newNews;

    @BeforeEach
    void setUp() {

        var categories = List.of(
                new Category(1, "category 1", new ArrayList<>()),
                new Category(2, "category 2", new ArrayList<>())
        );

        newsListInDB = List.of(
                new News(1L, "title 1", "content 1, content 1, content 1", LocalDateTime.now().minusDays(1L), categories.get(1)),
                new News(2L, "title 2", "content 2, content 2, content 2", LocalDateTime.now().minusHours(1L), categories.get(0)),
                new News(3L, "title 3", "content 3, content 3, content 3", LocalDateTime.now().minusMinutes(1L), categories.get(0))
        );

        newNews = new News(null, "new title", "new content, new content, new content", LocalDateTime.now(), categories.get(1));

        Mockito.reset(newsRepository);

        when(newsRepository.findAll()).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findById(-1L)).thenReturn(Optional.empty());
        when(newsRepository.save(newNews)).thenReturn(newNews);
        doNothing().when(newsRepository).deleteById(anyLong());

        // All these functions will return all the news.
        when(newsRepository.findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(anyString(), anyString(), any(Category.class))).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseOrderByPublishedAtDesc(anyString(), anyString())).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findAllByTitleLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(anyString(), any(Category.class))).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findAllByTitleLikeIgnoreCaseOrderByPublishedAtDesc(anyString())).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findAllByContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(anyString(), any(Category.class))).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findAllByContentLikeIgnoreCaseOrderByPublishedAtDesc(anyString())).thenReturn(new ArrayList<>(newsListInDB));
        when(newsRepository.findAllByCategoryOrderByPublishedAtDesc(any(Category.class))).thenReturn(new ArrayList<>(newsListInDB));

        for (var news : newsListInDB) {
            when(newsRepository.findById(news.getId())).thenReturn(Optional.of(news));
            when(newsRepository.save(news)).thenReturn(news);
        }
    }

    @Test
    void testGetById() {
        for (var news : newsListInDB) {
            assertEquals(news, newsService.getById(news.getId()));
            verify(newsRepository, times(1)).findById(news.getId());
        }
    }

    @Test
    void testGetByIdNotFound() {
        assertNull(newsService.getById(-1L));
        verify(newsRepository, times(1)).findById(-1L);
    }

    @Test
    void testGetAll() {
        assertEquals(newsListInDB, newsService.getAll());
        verify(newsRepository, times(1)).findAll();
    }

    @Test
    void testGetAllByParams() {
        newsService.getAllByParams("title", "content", new Category());
        verify(newsRepository, times(1)).findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc("%title%", "%content%", new Category());
    }

    @Test
    void testGetAllByParamsWithoutParams() {
        newsService.getAllByParams(null, null, null);
        newsService.getAllByParams("", "", null);
        verify(newsRepository, times(2)).findAll();
    }

    @Test
    void testGetAllByParamsOnlyTitle() {
        newsService.getAllByParams("title", null, null);
        verify(newsRepository, times(1)).findAllByTitleLikeIgnoreCaseOrderByPublishedAtDesc("%title%");
    }

    @Test
    void testGetAllByParamsOnlyContent() {
        newsService.getAllByParams(null, "content", null);
        verify(newsRepository, times(1)).findAllByContentLikeIgnoreCaseOrderByPublishedAtDesc("%content%");
    }

    @Test
    void testGetAllByParamsOnlyCategory() {
        newsService.getAllByParams(null, null, new Category());
        verify(newsRepository, times(1)).findAllByCategoryOrderByPublishedAtDesc(new Category());
    }

    @Test
    void testGetAllByParamsTitleAndContent() {
        newsService.getAllByParams("title", "content", null);
        verify(newsRepository, times(1)).findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseOrderByPublishedAtDesc("%title%", "%content%");
    }

    @Test
    void testGetAllByParamsTitleAndCategory() {
        newsService.getAllByParams("title", null, new Category());
        verify(newsRepository, times(1)).findAllByTitleLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc("%title%", new Category());
    }

    @Test
    void testGetAllByParamsContentAndCategory() {
        newsService.getAllByParams(null, "content", new Category());
        verify(newsRepository, times(1)).findAllByContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc("%content%", new Category());
    }

    @Test
    void testCreate() {
        assertEquals(newNews, newsService.create(newNews));
        verify(newsRepository, times(1)).save(newNews);
    }

    @Test
    void testUpdate() {
        for (var news : newsListInDB) {
            assertEquals(news, newsService.update(news.getId(), news));
            verify(newsRepository, times(1)).findById(news.getId());
            verify(newsRepository, times(1)).save(news);
        }
    }

    @Test
    void testUpdateNotFound() {
        assertThrows(NewsNotFoundException.class, () -> newsService.update(-1L, newNews));
        verify(newsRepository, times(1)).findById(-1L);
        verify(newsRepository, never()).save(newNews);
    }

    @Test
    void testDeleteById() {
        for (var news : newsListInDB) {
            newsService.deleteById(news.getId());
            verify(newsRepository, times(1)).deleteById(news.getId());
        }
    }

    @Test
    void testDeleteByIdNotFound() {
        newsService.deleteById(-1L);
        verify(newsRepository, times(1)).deleteById(-1L);
    }
}