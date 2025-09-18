package com.personal.ecommerce.service;


import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.personal.ecommerce.entity.Category;
import com.personal.ecommerce.repository.CategoryRepository;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository _categoryRepository;

    public List<Category> fetchCategories(){
        return _categoryRepository.findAll();
    }

    public String addCategory(String categoryName){
        Category category = _categoryRepository.findByCategoryName(categoryName);
        if(category != null){
            return "Category already exists";
        }
        Category newCategory = new Category();
        newCategory.setCategoryName(categoryName);
        newCategory.setProductList(new ArrayList<>());
        _categoryRepository.save(newCategory);
        return "Category added successfully";
    }
    
}
