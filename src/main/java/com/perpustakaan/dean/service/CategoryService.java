package com.perpustakaan.dean.service;

import com.perpustakaan.dean.dto.CategoryRequestDto;
import com.perpustakaan.dean.dto.CategoryResponseDto;
import com.perpustakaan.dean.models.Category;
import com.perpustakaan.dean.repositories.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryResponseDto createCategory(CategoryRequestDto dto) {
        Category category = Category.builder()
                .name(dto.getName())
                .build();

        categoryRepository.save(category);
        return new CategoryResponseDto(category.getId(), category.getName());
    }

    public List<CategoryResponseDto> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(cat -> new CategoryResponseDto(cat.getId(), cat.getName()))
                .collect(Collectors.toList());
    }

    public CategoryResponseDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        return new CategoryResponseDto(category.getId(), category.getName());
    }

    public CategoryResponseDto updateCategory(Long id, CategoryRequestDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        category.setName(dto.getName());
        categoryRepository.save(category);

        return new CategoryResponseDto(category.getId(), category.getName());
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Kategori tidak ditemukan"));

        if (category.getBooks() != null && !category.getBooks().isEmpty()) {
            throw new IllegalStateException("Kategori tidak bisa dihapus karena masih dipakai oleh buku");
        }

        categoryRepository.delete(category);
    }
}
