package com.example.controller;

import com.example.controller.converter.CategoryConverter;
import com.example.exception.NewsNotFoundException;
import com.example.model.Category;
import com.example.model.News;
import com.example.service.CategoryService;
import com.example.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class NewsControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private NewsService newsService;
    @MockBean
    private CategoryService categoryService;
    @MockBean
    private CategoryConverter categoryConverter;

    private List<News> newsListInDB;
    private List<Category> categoriesInDB;

    private News newNews;

    @BeforeEach
    void setUp() {

        categoriesInDB = List.of(
                new Category(1, "category 1", new ArrayList<>()),
                new Category(2, "category 2", new ArrayList<>())
        );

        newsListInDB = List.of(
                new News(1L, "title 1", "content 1, content 1, content 1", LocalDateTime.now().minusDays(1L).truncatedTo(ChronoUnit.SECONDS), categoriesInDB.get(1)),
                new News(2L, "title 2", "content 2, content 2, content 2", LocalDateTime.now().minusHours(1L).truncatedTo(ChronoUnit.SECONDS), categoriesInDB.get(0)),
                new News(3L, "title 3", "content 3, content 3, content 3", LocalDateTime.now().minusMinutes(1L).truncatedTo(ChronoUnit.SECONDS), categoriesInDB.get(0))
        );

        newNews = new News(null, "new title", "new content, new content, new content", LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS), categoriesInDB.get(1));

        Mockito.reset(newsService, categoryService);

        // news
        when(newsService.getAll()).thenReturn(new ArrayList<>(newsListInDB));
        when(newsService.getAllByParams(any(), any(), any())).thenReturn(new ArrayList<>(newsListInDB));
        when(newsService.getById(-1L)).thenReturn(null);
        when(newsService.create(any(News.class))).thenReturn(newNews);
        when(newsService.update(-1L, newNews)).thenThrow(NewsNotFoundException.class);
        doNothing().when(newsService).deleteById(anyLong());

        for (var news : newsListInDB) {
            when(newsService.getById(news.getId())).thenReturn(news);
            when(newsService.update(news.getId(), news)).thenReturn(news);
        }

        // categories
        when(categoryService.getAll()).thenReturn(new ArrayList<>(categoriesInDB));
        when(categoryConverter.convert("")).thenReturn(null);
        when(categoryConverter.convert("all categories")).thenReturn(null);

        for (var category : categoriesInDB) {
            when(categoryConverter.convert(category.getName())).thenReturn(category);
        }
    }

    @Test
    void testGetById() throws Exception {

        for (var news : newsListInDB) {

            mvc.perform(get("/news/{id}", news.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("news/news"))
                    .andExpect(model().attribute("news", news))
                    .andExpect(content().string(containsString(news.getTitle())))
                    .andExpect(content().string(containsString(news.getContent())))
                    .andExpect(content().string(containsString(news.getCategory().getName())));

            verify(newsService, times(1)).getById(news.getId());
        }
    }

    @Test
    void testGetByIdNotFound() throws Exception {

        mvc.perform(get("/news/{id}", -1L))
                .andExpect(view().name("news/error"));

        verify(newsService, times(1)).getById(-1L);
    }

    @Test
    void testGetAllWithoutParams() throws Exception {

        var resultActions = mvc.perform(get("/news"))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(null, null, null);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllWithParams() throws Exception {

        var title = "a";
        var content = "b";
        var category = categoriesInDB.get(0);

        var resultActions = mvc.perform(get("/news")
                        .param("title", title)
                        .param("content", content)
                        .param("category", category.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(title, content, category);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllByTitle() throws Exception {

        var title = "a";

        var resultActions = mvc.perform(get("/news")
                        .param("title", title))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(title, null, null);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllByContent() throws Exception {

        var content = "b";

        var resultActions = mvc.perform(get("/news")
                        .param("content", content))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(null, content, null);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllByCategory() throws Exception {

        var category = categoriesInDB.get(0);

        var resultActions = mvc.perform(get("/news")
                        .param("category", category.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(null, null, category);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllByTitleAndContent() throws Exception {

        var title = "a";
        var content = "b";

        var resultActions = mvc.perform(get("/news")
                        .param("title", title)
                        .param("content", content))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(title, content, null);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllByTitleAndCategory() throws Exception {

        var title = "a";
        var category = categoriesInDB.get(0);

        var resultActions = mvc.perform(get("/news")
                        .param("title", title)
                        .param("category", category.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(title, null, category);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testGetAllByContentAndCategory() throws Exception {

        var content = "a";
        var category = categoriesInDB.get(0);

        var resultActions = mvc.perform(get("/news")
                        .param("content", content)
                        .param("category", category.getName()))
                .andExpect(status().isOk())
                .andExpect(view().name("news/list"))
                .andExpect(model().attribute("listNews", newsListInDB))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Find the news")));

        for (var news : newsListInDB) {
            resultActions
                    .andExpect(content().string(containsString(news.getTitle())));
        }

        verify(newsService, times(1)).getAllByParams(null, content, category);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testInitCreation() throws Exception {

        mvc.perform(get("/news/add"))
                .andExpect(status().isOk())
                .andExpect(view().name("news/createForm"))
                .andExpect(model().attribute("news", new News()))
                .andExpect(model().attribute("categories", categoriesInDB))
                .andExpect(content().string(containsString("Adding news")));

        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testProcessCreation() throws Exception {

        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        mvc.perform(post("/news")
                        .param("title", newNews.getTitle())
                        .param("content", newNews.getContent())
                        .param("category", newNews.getCategory().getName())
                        .param("publishedAt", newNews.getPublishedAt().format(dateFormatter)))
                .andExpect(status().isFound())
                .andExpect(view().name(containsString("redirect:/news/")));

        verify(newsService, times(1)).create(newNews);
        verify(categoryService, never()).getAll();
    }

    @Test
    void testProcessCreationValidationErrors() throws Exception {

        mvc.perform(post("/news")
                        .param("title", "")
                        .param("content", "")
                        .param("category", "")
                        .param("publishedAt", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("news/createForm"));

        verify(newsService, never()).create(newNews);
        verify(categoryService, times(1)).getAll();
    }

    @Test
    void testInitUpdating() throws Exception {

        for (var news : newsListInDB) {

            mvc.perform(get("/news/{id}/edit", news.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("news/updateForm"))
                    .andExpect(model().attribute("news", news))
                    .andExpect(model().attribute("categories", categoriesInDB))
                    .andExpect(content().string(containsString("Updating news")));

            verify(newsService, times(1)).getById(news.getId());
        }
    }

    @Test
    void testInitUpdatingNotFound() throws Exception {

        mvc.perform(get("/news/{id}/edit", -1L))
                .andExpect(view().name("news/error"));

        verify(newsService, times(1)).getById(-1L);
    }

    @Test
    void testProcessUpdating() throws Exception {

        var dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        for (var news : newsListInDB) {

            mvc.perform(patch("/news/{id}", news.getId())
                            .param("title", news.getTitle())
                            .param("content", news.getContent())
                            .param("category", news.getCategory().getName())
                            .param("publishedAt", news.getPublishedAt().format(dateFormatter)))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/news/" + news.getId()));

            verify(newsService, times(1)).update(news.getId(), news);
        }

        verify(categoryService, never()).getAll();
    }

    @Test
    void testProcessUpdatingValidationErrors() throws Exception {

        for (var news : newsListInDB) {

            mvc.perform(patch("/news/{id}", news.getId())
                            .param("title", "")
                            .param("content", "")
                            .param("category", "")
                            .param("publishedAt", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("news/updateForm"));
        }

        verify(newsService, never()).update(anyLong(), any(News.class));
        verify(categoryService, times(newsListInDB.size())).getAll();
    }

    @Test
    void testDelete() throws Exception {

        for (var news : newsListInDB) {

            mvc.perform(delete("/news/{id}", news.getId()))
                    .andExpect(status().isFound())
                    .andExpect(view().name("redirect:/news"));

            verify(newsService, times(1)).deleteById(news.getId());
        }
    }

    @Test
    void testDeleteNotFound() throws Exception {

        mvc.perform(delete("/news/{id}", -1L))
                .andExpect(view().name("redirect:/news"));

        verify(newsService, times(1)).deleteById(-1L);
    }
}