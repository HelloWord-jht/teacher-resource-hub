package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.CategorySaveRequest;
import com.teacherresourcehub.vo.CategoryVO;

import java.util.List;

public interface CategoryService {

    List<CategoryVO> listEnabledCategories();

    List<CategoryVO> listAdminCategories();

    List<CategoryVO> listByIds(List<Long> ids);

    void createCategory(CategorySaveRequest request);

    void updateCategory(Long id, CategorySaveRequest request);

    void updateCategoryStatus(Long id, Integer status);

    void deleteCategory(Long id);

    long countTotal();
}
