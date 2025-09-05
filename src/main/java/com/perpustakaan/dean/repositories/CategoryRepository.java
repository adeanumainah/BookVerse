package com.perpustakaan.dean.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.perpustakaan.dean.models.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}
