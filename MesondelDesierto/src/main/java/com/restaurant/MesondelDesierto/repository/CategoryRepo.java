package com.restaurant.MesondelDesierto.repository;

import com.restaurant.MesondelDesierto.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepo extends JpaRepository<Category, Long> {
}
