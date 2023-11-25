package com.example.controller;

import com.example.exception.NewsNotFoundException;
import com.example.model.Category;
import com.example.model.News;
import com.example.service.CategoryService;
import com.example.service.NewsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@Controller
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;
    private final CategoryService categoryService;

    @GetMapping("/{id}")
    public String getById(@PathVariable long id, Model model) {
        var news = newsService.getById(id);
        if (news == null) {
            throw new NewsNotFoundException(id);
        }
        model.addAttribute("news", news);
        return "news/news";
    }

    @GetMapping
    public String getAll(Model model,
                         @RequestParam(value = "title", required = false) String title,
                         @RequestParam(value = "content", required = false) String content,
                         @RequestParam(value = "category", required = false) Category category) {

        var news = newsService.getAllByParams(title, content, category);

        model.addAttribute("listNews", news);

        model.addAttribute("title", Objects.requireNonNullElse(title, "").isEmpty() ? null : title);
        model.addAttribute("content", Objects.requireNonNullElse(content, "").isEmpty() ? null : content);
        model.addAttribute("category", category);

        model.addAttribute("categories", categoryService.getAll());

        return "news/list";
    }

    @GetMapping("/add")
    public String initCreation(Model model) {
        model.addAttribute("news", new News());
        model.addAttribute("categories", categoryService.getAll());
        return "news/createForm";
    }

    @PostMapping
    public String processCreation(Model model,
                         @ModelAttribute @Valid News news, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("news", news);
            model.addAttribute("categories", categoryService.getAll());
            return "news/createForm";
        }
        news = newsService.create(news);
        return "redirect:/news/" + news.getId();
    }

    @GetMapping("/{id}/edit")
    public String initUpdating(@PathVariable Long id, Model model) {
        var news = newsService.getById(id);
        if (news == null) {
            throw new NewsNotFoundException(id);
        }
        model.addAttribute("news", news);
        model.addAttribute("categories", categoryService.getAll());
        return "news/updateForm";
    }

    @PatchMapping("/{id}")
    public String processUpdating(@PathVariable Long id,
                                  @ModelAttribute @Valid News news, BindingResult bindingResult,
                                  Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("news", news);
            model.addAttribute("categories", categoryService.getAll());
            return "news/updateForm";
        }
        news = newsService.update(id, news);
        return "redirect:/news/" + news.getId();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        newsService.deleteById(id);
        return "redirect:/news";
    }
}
