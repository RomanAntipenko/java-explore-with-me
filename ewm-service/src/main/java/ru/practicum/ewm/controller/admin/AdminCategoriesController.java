package ru.practicum.ewm.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.category.CategoryDto;
import ru.practicum.ewm.dto.category.NewCategoryDto;
import ru.practicum.ewm.service.CategoryService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/categories")
@Validated
public class AdminCategoriesController {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategoriesAdmin(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info("Method of creating categories for Admin was caused in AdminCategoriesController");
        return categoryService.createCategoriesAdmin(newCategoryDto);
    }

    @DeleteMapping("/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoriesAdmin(@PathVariable Long catId) {
        log.info("Removal method for categories for Admin was caused in AdminCategoriesController");
        categoryService.deleteCategoriesAdmin(catId);
    }

    @PatchMapping("/{catId}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategoriesAdmin(@PathVariable Long catId,
                                             @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("Method of updating categories for Admin was caused in AdminCategoriesController");
        return categoryService.updateCategoriesAdmin(catId, newCategoryDto);
    }
}
