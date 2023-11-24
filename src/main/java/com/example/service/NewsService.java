package com.example.service;

import com.example.exception.NewsNotFoundException;
import com.example.model.Category;
import com.example.model.News;
import com.example.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;

    public News getById(Long id) {
        return newsRepository.findById(id)
                .orElse(null);
    }

    public List<News> getAll() {
        return newsRepository.findAll();
    }

    public List<News> getAllByParams(String title, String content, Category category) {

        List<News> result;

        var titleIsEmpty = Objects.requireNonNullElse(title, "").isEmpty();
        var contentIsEmpty = Objects.requireNonNullElse(content, "").isEmpty();
        var categoryIsEmpty = category == null;

        if (!titleIsEmpty) {
            title = "%" + title + "%";
        }
        if (!contentIsEmpty) {
            content = "%" + content + "%";
        }

        if (titleIsEmpty && contentIsEmpty && categoryIsEmpty) {
            result = newsRepository.findAll();
        } else if (!titleIsEmpty && !contentIsEmpty && !categoryIsEmpty) {
            result = newsRepository.findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(title, content, category);
        } else if (!titleIsEmpty && !contentIsEmpty) {
            result = newsRepository.findAllByTitleLikeIgnoreCaseAndContentLikeIgnoreCaseOrderByPublishedAtDesc(title, content);
        } else if (!titleIsEmpty && !categoryIsEmpty) {
            result = newsRepository.findAllByTitleLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(title, category);
        } else if (!contentIsEmpty && !categoryIsEmpty) {
            result = newsRepository.findAllByContentLikeIgnoreCaseAndCategoryOrderByPublishedAtDesc(content, category);
        } else if (!titleIsEmpty) {
            result = newsRepository.findAllByTitleLikeIgnoreCaseOrderByPublishedAtDesc(title);
        } else if (!contentIsEmpty) {
            result = newsRepository.findAllByContentLikeIgnoreCaseOrderByPublishedAtDesc(content);
        } else if (!categoryIsEmpty) {
            result = newsRepository.findAllByCategoryOrderByPublishedAtDesc(category);
        } else {
            result = Collections.emptyList();
        }

        return result;
    }

    public News create(News news) {
        return newsRepository.save(news);
    }

    @Transactional
    public News update(Long id, News news) {
        var newsExisted = getById(id);
        if (newsExisted == null) {
            throw new NewsNotFoundException(id);
        }
        news.setId(id);
        if (news.getPublishedAt() == null) {
            news.setPublishedAt(newsExisted.getPublishedAt());
        }
        return newsRepository.save(news);
    }

    public void deleteById(Long id) {
        newsRepository.deleteById(id);
    }
}
