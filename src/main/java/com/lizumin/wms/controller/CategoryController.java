package com.lizumin.wms.controller;

import com.lizumin.wms.entity.Category;
import com.lizumin.wms.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Category Controller
 *
 * @author Zumin Li
 * @date 2024/2/17 17:46
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    private CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/parent/{parentId}")
    public ResponseEntity<List<Category>> getCategoriesByParentId(@PathVariable int parentId) {
        return ResponseEntity.ok(this.categoryService.getCategoriesByParentId(parentId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryByUd(@PathVariable int id) {
        return ResponseEntity.ok(this.categoryService.getCategory(id));
    }
}
