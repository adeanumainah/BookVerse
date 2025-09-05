package com.perpustakaan.dean.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "books")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String author;

    private String cover;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private Integer releaseYear;
}
