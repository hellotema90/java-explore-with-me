package ru.practicum.category.service;

import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.model.Category;

import java.util.Collection;

public interface CategoryService {
    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(NewCategoryDto updateCategoryDto, Long categoryId);

    Category getCategoryById(Long categoryId);

    CategoryDto getCategoryDtoById(Long categoryById);

    Collection<CategoryDto> getAllCategory(Integer from, Integer size);

    void delete(Long id);
}