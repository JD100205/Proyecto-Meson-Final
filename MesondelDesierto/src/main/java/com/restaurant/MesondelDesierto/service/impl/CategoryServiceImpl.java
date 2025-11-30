package com.restaurant.MesondelDesierto.service.impl;

import com.restaurant.MesondelDesierto.dto.CategoryDto;
import com.restaurant.MesondelDesierto.dto.Response;
import com.restaurant.MesondelDesierto.entity.Category;
import com.restaurant.MesondelDesierto.exception.NotFoundException;
import com.restaurant.MesondelDesierto.mapper.EntityDtoMapper;
import com.restaurant.MesondelDesierto.repository.CategoryRepo;
import com.restaurant.MesondelDesierto.service.interf.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepo categoryRepo;
    private final EntityDtoMapper entityDtoMapper;




    @Override
    public Response createCategory(CategoryDto categoryRequest) {
        Category category = new Category();
        category.setName(categoryRequest.getName());
        categoryRepo.save(category);
        return Response.builder()
                .status(200)
                .message("Categoria creada correctamente")
                .build();
    }

    @Override
    public Response updateCategory(Long categoryId, CategoryDto categoryRequest) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Categoria no encontrada"));
        category.setName(categoryRequest.getName());
        categoryRepo.save(category);
        return Response.builder()
                .status(200)
                .message("Categoria actualizada correctamente")
                .build();
    }

    @Override
    public Response getAllCategories() {
        List<Category> categories = categoryRepo.findAll();
        List<CategoryDto> categoryDtoList = categories.stream()
                .map(entityDtoMapper::mapCategoryToDtoBasic)
                .collect(Collectors.toList());

        return  Response.builder()
                .status(200)
                .categoryList(categoryDtoList)
                .build();
    }

    @Override
    public Response getCategoryById(Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Categoria no encontrada"));
        CategoryDto categoryDto = entityDtoMapper.mapCategoryToDtoBasic(category);
        return Response.builder()
                .status(200)
                .category(categoryDto)
                .build();
    }

    @Override
    public Response deleteCategory(Long categoryId) {
        Category category = categoryRepo.findById(categoryId).orElseThrow(()-> new NotFoundException("Categoria no encontrada"));
        categoryRepo.delete(category);
        return Response.builder()
                .status(200)
                .message("Categoria fue eliminada correctamente")
                .build();
    }
}
