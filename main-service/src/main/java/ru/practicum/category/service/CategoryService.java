package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long id, CategoryDto dto);

    void deleteCategory(Long id);

    List<CategoryDto> getAll(int from, int size);

    CategoryDto getById(Long id);
}
