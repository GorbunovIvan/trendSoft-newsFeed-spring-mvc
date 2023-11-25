package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "news")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode(of = { "title", "publishedAt", "category" })
@ToString
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    @NotNull
    @Size(min = 3, max = 199, message = "The title size must be between 3 and 199 characters")
    private String title;

    @Column(name = "content")
    @NotNull
    @Size(min = 29, max = 9_999, message = "The content size must be between 29 and 9999 characters")
    private String content;

    @Column(name = "published_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedAt;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "category_id")
    @NotNull
    private Category category;

    @PrePersist
    public void prePersist() {
        if (getPublishedAt() == null) {
            setPublishedAt(LocalDateTime.now());
        }
        setPublishedAt(getPublishedAt().truncatedTo(ChronoUnit.SECONDS));
    }

    public String shortView() {

        var sb = new StringBuilder();

        if (getPublishedAt() != null) {
            sb.append(getPublishedAtShortened());
            sb.append(" - ");
        }

        sb.append(getTitle());

        return sb.toString();
    }

    public String getPublishedAtShortened() {

        if (getPublishedAt() == null) {
            return "";
        }

        var sb = new StringBuilder();

        if (getPublishedAt().toLocalDate().equals(LocalDate.now())) {
            sb.append(getPublishedAt().toLocalTime().truncatedTo(ChronoUnit.SECONDS));
        } else if (getPublishedAt().toLocalDate().equals(LocalDate.now().minusDays(1L))) {
            sb.append("yesterday");
        } else {
            sb.append(getPublishedAt().toLocalDate());
        }

        return sb.toString();
    }
}
