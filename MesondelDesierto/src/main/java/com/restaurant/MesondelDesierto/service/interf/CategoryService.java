package com.restaurant.MesondelDesierto.service.interf;

import com.restaurant.MesondelDesierto.dto.CategoryDto;
import com.restaurant.MesondelDesierto.dto.Response;

public interface CategoryService {
    Response createCategory(CategoryDto categoryRequest);
    Response updateCategory(Long categoryId, CategoryDto categoryRequest);
    Response getAllCategories();
    Response getCategoryById(Long categoryId);
    Response deleteCategory(Long categoryId);
}
