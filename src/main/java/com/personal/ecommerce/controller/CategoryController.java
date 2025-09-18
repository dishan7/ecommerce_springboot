package com.personal.ecommerce.controller;

import com.personal.ecommerce.entity.Category;
import com.personal.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    @Autowired
    private CategoryService _categoryService;

    @GetMapping("/categories")
    public List<Category> fetchCategories(){
        return _categoryService.fetchCategories();
    }
    
}
