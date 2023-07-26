package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.error.ConditionNotMetException;
import ru.practicum.ewm.error.FieldUniqueException;
import ru.practicum.ewm.error.ObjectNotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.repository.CategoryRepository;
import ru.practicum.ewm.repository.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;

    @Override
    public CategoryDto createCategoriesAdmin(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.save(CategoryMapper.toCategoryFromNew(newCategoryDto));
            return CategoryMapper.toDtoFromCategory(category);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public void deleteCategoriesAdmin(Long catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new ObjectNotFoundException(String.format("Category with id=\"%s\" was not found", catId));
        }
        Event event = eventRepository.findFirstByCategoryId(catId);
        if (event != null) {
            throw new ConditionNotMetException("Category already has event");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto updateCategoriesAdmin(Long catId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new ObjectNotFoundException(
                String.format("Category with id = \"%s\" not found", catId)));
        category.setName(newCategoryDto.getName());
        try {
            Category newCategory = categoryRepository.save(category);
            return CategoryMapper.toDtoFromCategory(newCategory);
        } catch (DataIntegrityViolationException e) {
            throw new FieldUniqueException(e.getMessage());
        }
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageRequest).toList();
        return categories.stream()
                .map(CategoryMapper::toDtoFromCategory)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        Category category = categoryRepository.findById(catId).orElseThrow(() -> new ObjectNotFoundException(
                String.format("Category with id = \"%s\" not found", catId)));
        return CategoryMapper.toDtoFromCategory(category);
    }

}
