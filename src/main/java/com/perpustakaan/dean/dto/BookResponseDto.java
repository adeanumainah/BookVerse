package com.perpustakaan.dean.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookResponseDto {
    private Long id;
    private String title;
    private String author;
    private String categoryName;
    private String cover;
    private Integer releaseYear; 
}

