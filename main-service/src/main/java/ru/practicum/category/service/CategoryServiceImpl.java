package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.exception.NotFoundException;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        String name = newCategoryDto.getName();
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException(String.format("Категория с именем: %s уже существует", name));
        }
        return CategoryMapper.toCategoryDto(categoryRepository.save(CategoryMapper.toCategory(newCategoryDto)));
    }

    @Transactional
    @Override
    public CategoryDto update(NewCategoryDto newCategoryDto, Long categoryId) {
        String name = newCategoryDto.getName();
        if (categoryRepository.existsByName(name)) {
            throw new ConflictException(String.format("Категория с именем: %s уже существует", name));
        }
        Category category = getCategoryById(categoryId);
        category.setName(name);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(()
                -> new NotFoundException(String.format("Категория с именем: %s не существует", categoryId)));
    }

    @Override
    public CategoryDto getCategoryDtoById(Long id) {
        return CategoryMapper.toCategoryDto(getCategoryById(id));
    }

    @Override
    public Collection<CategoryDto> getAllCategory(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size);
        return CategoryMapper.toListCategoryDto(categoryRepository.findAll(page).toList());
    }

    @Transactional
    @Override
    public void delete(Long id) {
        Category category = getCategoryById(id);
        if (eventRepository.existsByCategory(category)) {
            throw new ConflictException(String.format(
                    "Не возможно удалить категорию с id: %s. Есть события относящиеся к этой категории", id));
        }
        categoryRepository.deleteById(id);
    }
}