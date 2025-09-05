package com.perpustakaan.dean.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookRequestDto {
    private Long id;
    private String title;
    private String author;
    private Long categoryId;
    private MultipartFile coverFile;
    private Integer releaseYear; 
}


