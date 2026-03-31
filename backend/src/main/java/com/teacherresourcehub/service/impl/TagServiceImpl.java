package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.TagSaveRequest;
import com.teacherresourcehub.entity.ResourceTag;
import com.teacherresourcehub.entity.Tag;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.ResourceTagMapper;
import com.teacherresourcehub.mapper.TagMapper;
import com.teacherresourcehub.service.TagService;
import com.teacherresourcehub.vo.TagVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;
    private final ResourceTagMapper resourceTagMapper;

    public TagServiceImpl(TagMapper tagMapper, ResourceTagMapper resourceTagMapper) {
        this.tagMapper = tagMapper;
        this.resourceTagMapper = resourceTagMapper;
    }

    @Override
    public List<TagVO> listEnabledTags() {
        return toTagVOList(tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getStatus, 1)
                .orderByDesc(Tag::getSortOrder, Tag::getId)));
    }

    @Override
    public List<TagVO> listAdminTags() {
        return toTagVOList(tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .orderByDesc(Tag::getSortOrder, Tag::getId)));
    }

    @Override
    public List<TagVO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Tag> list = tagMapper.selectList(new LambdaQueryWrapper<Tag>()
                .in(Tag::getId, ids));
        Map<Long, TagVO> map = toTagVOList(list).stream()
                .collect(Collectors.toMap(TagVO::getId, Function.identity(), (a, b) -> a));
        return ids.stream().map(map::get).filter(Objects::nonNull).toList();
    }

    @Override
    public void createTag(TagSaveRequest request) {
        validateUnique(null, request.getName());
        Tag tag = new Tag();
        BeanUtils.copyProperties(request, tag);
        tagMapper.insert(tag);
    }

    @Override
    public void updateTag(Long id, TagSaveRequest request) {
        Tag tag = getTagOrThrow(id);
        validateUnique(id, request.getName());
        BeanUtils.copyProperties(request, tag);
        tagMapper.updateById(tag);
    }

    @Override
    public void updateTagStatus(Long id, Integer status) {
        Tag tag = getTagOrThrow(id);
        tag.setStatus(status);
        tagMapper.updateById(tag);
    }

    @Override
    public void deleteTag(Long id) {
        getTagOrThrow(id);
        Long count = resourceTagMapper.selectCount(new LambdaQueryWrapper<ResourceTag>()
                .eq(ResourceTag::getTagId, id));
        if (count != null && count > 0) {
            throw new BusinessException("该标签已被资源使用，暂不允许删除");
        }
        tagMapper.deleteById(id);
    }

    @Override
    public long countTotal() {
        Long count = tagMapper.selectCount(new LambdaQueryWrapper<Tag>());
        return count == null ? 0L : count;
    }

    private void validateUnique(Long id, String name) {
        Tag tag = tagMapper.selectOne(new LambdaQueryWrapper<Tag>()
                .eq(Tag::getName, name)
                .last("LIMIT 1"));
        if (tag != null && !tag.getId().equals(id)) {
            throw new BusinessException("标签名称已存在");
        }
    }

    private Tag getTagOrThrow(Long id) {
        Tag tag = tagMapper.selectById(id);
        if (tag == null) {
            throw new BusinessException("标签不存在");
        }
        return tag;
    }

    private List<TagVO> toTagVOList(List<Tag> list) {
        return list.stream().map(item -> {
            TagVO vo = new TagVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).toList();
    }
}
