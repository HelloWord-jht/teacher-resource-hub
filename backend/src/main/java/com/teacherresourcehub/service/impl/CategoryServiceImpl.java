package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.CategorySaveRequest;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.CategoryService;
import com.teacherresourcehub.vo.CategoryVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final ResourceMapper resourceMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper, ResourceMapper resourceMapper) {
        this.categoryMapper = categoryMapper;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public List<CategoryVO> listEnabledCategories() {
        List<Category> list = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByDesc(Category::getSortOrder, Category::getId));
        return toCategoryVOList(list);
    }

    @Override
    public List<CategoryVO> listAdminCategories() {
        return toCategoryVOList(categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .orderByDesc(Category::getSortOrder, Category::getId)));
    }

    @Override
    public List<CategoryVO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Category> list = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .in(Category::getId, ids));
        Map<Long, CategoryVO> map = toCategoryVOList(list).stream()
                .collect(Collectors.toMap(CategoryVO::getId, Function.identity(), (a, b) -> a));
        return ids.stream().map(map::get).filter(Objects::nonNull).toList();
    }

    @Override
    public void createCategory(CategorySaveRequest request) {
        validateUnique(null, request.getName(), request.getSlug());
        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(Long id, CategorySaveRequest request) {
        Category category = getCategoryOrThrow(id);
        validateUnique(id, request.getName(), request.getSlug());
        BeanUtils.copyProperties(request, category);
        categoryMapper.updateById(category);
    }

    @Override
    public void updateCategoryStatus(Long id, Integer status) {
        Category category = getCategoryOrThrow(id);
        category.setStatus(status);
        categoryMapper.updateById(category);
    }

    @Override
    public void deleteCategory(Long id) {
        getCategoryOrThrow(id);
        Long count = resourceMapper.selectCount(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getCategoryId, id));
        if (count != null && count > 0) {
            throw new BusinessException("该分类下存在资源，暂不允许删除");
        }
        categoryMapper.deleteById(id);
    }

    @Override
    public long countTotal() {
        Long count = categoryMapper.selectCount(new LambdaQueryWrapper<Category>());
        return count == null ? 0L : count;
    }

    private void validateUnique(Long id, String name, String slug) {
        Category byName = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getName, name)
                .last("LIMIT 1"));
        if (byName != null && !byName.getId().equals(id)) {
            throw new BusinessException("分类名称已存在");
        }

        Category bySlug = categoryMapper.selectOne(new LambdaQueryWrapper<Category>()
                .eq(Category::getSlug, slug)
                .last("LIMIT 1"));
        if (bySlug != null && !bySlug.getId().equals(id)) {
            throw new BusinessException("分类别名已存在");
        }
    }

    private Category getCategoryOrThrow(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return category;
    }

    private List<CategoryVO> toCategoryVOList(List<Category> list) {
        return list.stream().map(item -> {
            CategoryVO vo = new CategoryVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).toList();
    }
}
