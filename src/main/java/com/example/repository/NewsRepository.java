package com.example.repository;

import com.example.model.Category;
import com.example.model.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {
    List<News> findAllByTitleLikeIgnoreCaseOrderByPublishedAtDesc(String title);
    List<News> findAllByContentLikeIgnoreCaseOrderByPublishedAtDesc(String content);
    List<News> findAllByCategoryOrderByPublishedAtDesc(Category category);
    List<News> findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseOrderByPublishedAtDesc(String title, String content);
    List<News> findAllByTitleLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(String title, Category category);
    List<News> findAllByContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(String content, Category category);
    List<News> findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(String title, String content, Category category);
}
