package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.CategoryService;
import com.teacherresourcehub.service.DashboardService;
import com.teacherresourcehub.service.LeadService;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.service.TagService;
import com.teacherresourcehub.vo.DashboardRecentResourceVO;
import com.teacherresourcehub.vo.DashboardVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final ResourceService resourceService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final LeadService leadService;
    private final ResourceMapper resourceMapper;
    private final CategoryMapper categoryMapper;

    public DashboardServiceImpl(ResourceService resourceService,
                                CategoryService categoryService,
                                TagService tagService,
                                LeadService leadService,
                                ResourceMapper resourceMapper,
                                CategoryMapper categoryMapper) {
        this.resourceService = resourceService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.leadService = leadService;
        this.resourceMapper = resourceMapper;
        this.categoryMapper = categoryMapper;
    }

    @Override
    public DashboardVO getDashboard() {
        DashboardVO vo = new DashboardVO();
        vo.setResourceTotal(resourceService.countTotal());
        vo.setCategoryTotal(categoryService.countTotal());
        vo.setTagTotal(tagService.countTotal());
        vo.setLeadTotal(leadService.countTotal());
        vo.setRecentResources(listRecentResources());
        vo.setRecentLeads(leadService.listRecentLeads(5));
        return vo;
    }

    private List<DashboardRecentResourceVO> listRecentResources() {
        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .orderByDesc(Resource::getCreatedAt, Resource::getId)
                .last("LIMIT 5"));
        Set<Long> categoryIds = resources.stream().map(Resource::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryNameMap = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                        .in(!categoryIds.isEmpty(), Category::getId, categoryIds))
                .stream()
                .collect(Collectors.toMap(Category::getId, Category::getName, (a, b) -> a));
        return resources.stream().map(item -> {
            DashboardRecentResourceVO vo = new DashboardRecentResourceVO();
            vo.setId(item.getId());
            vo.setTitle(item.getTitle());
            vo.setCategoryName(categoryNameMap.getOrDefault(item.getCategoryId(), ""));
            vo.setStatus(item.getStatus());
            vo.setPublishTime(item.getPublishTime());
            vo.setCreatedAt(item.getCreatedAt());
            return vo;
        }).toList();
    }
}
