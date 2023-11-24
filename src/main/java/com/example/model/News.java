package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "news")
@NoArgsConstructor @AllArgsConstructor
@Getter @Setter
@EqualsAndHashCode
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
    @Size(min = 49, max = 9_999, message = "The content size must be between 49 and 9999 characters")
    private String content;

    @Column(name = "published_at")
    @DateTimeFormat(pattern = "yyyy-MM-dd-HH:mm:ss")
    private String publishedAt;

    @ManyToOne(cascade = { CascadeType.MERGE, CascadeType.REFRESH })
    @JoinColumn(name = "category_id")
    private Category category;
}
