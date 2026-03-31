package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.PageContentSaveRequest;
import com.teacherresourcehub.entity.PageContent;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.PageContentMapper;
import com.teacherresourcehub.service.PageContentService;
import com.teacherresourcehub.vo.PageContentVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PageContentServiceImpl implements PageContentService {

    private final PageContentMapper pageContentMapper;

    public PageContentServiceImpl(PageContentMapper pageContentMapper) {
        this.pageContentMapper = pageContentMapper;
    }

    @Override
    public List<PageContentVO> listAll() {
        return pageContentMapper.selectList(new LambdaQueryWrapper<PageContent>()
                        .orderByAsc(PageContent::getPageCode))
                .stream()
                .map(this::toPageContentVO)
                .toList();
    }

    @Override
    public PageContentVO getByCode(String pageCode) {
        PageContent pageContent = pageContentMapper.selectOne(new LambdaQueryWrapper<PageContent>()
                .eq(PageContent::getPageCode, pageCode)
                .last("LIMIT 1"));
        if (pageContent == null) {
            throw new BusinessException("页面内容不存在");
        }
        return toPageContentVO(pageContent);
    }

    @Override
    public void saveOrUpdate(String pageCode, PageContentSaveRequest request) {
        PageContent pageContent = pageContentMapper.selectOne(new LambdaQueryWrapper<PageContent>()
                .eq(PageContent::getPageCode, pageCode)
                .last("LIMIT 1"));
        if (pageContent == null) {
            pageContent = new PageContent();
            pageContent.setPageCode(pageCode);
            pageContent.setTitle(request.getTitle());
            pageContent.setContentHtml(request.getContentHtml());
            pageContentMapper.insert(pageContent);
            return;
        }

        pageContent.setTitle(request.getTitle());
        pageContent.setContentHtml(request.getContentHtml());
        pageContentMapper.updateById(pageContent);
    }

    private PageContentVO toPageContentVO(PageContent pageContent) {
        PageContentVO vo = new PageContentVO();
        BeanUtils.copyProperties(pageContent, vo);
        return vo;
    }
}
