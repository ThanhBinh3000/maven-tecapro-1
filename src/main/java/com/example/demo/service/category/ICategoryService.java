package com.example.demo.service.category;


import com.example.demo.model.Category;
import com.example.demo.service.IGeneralService;

public interface ICategoryService extends IGeneralService<Category> {
    Iterable<Category> findAllByNameContaining(String name);
    void deleteCategory(Long id);
}
