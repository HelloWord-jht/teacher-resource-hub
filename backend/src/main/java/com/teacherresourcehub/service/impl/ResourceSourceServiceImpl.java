package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.common.enums.AuthorizationStatus;
import com.teacherresourcehub.common.enums.RiskLevel;
import com.teacherresourcehub.dto.ResourceSourceAuditRequest;
import com.teacherresourcehub.dto.ResourceSourceSaveRequest;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.entity.ResourceSource;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.mapper.ResourceSourceMapper;
import com.teacherresourcehub.service.ResourceSourceService;
import com.teacherresourcehub.vo.ResourceSourceVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResourceSourceServiceImpl implements ResourceSourceService {

    private final ResourceSourceMapper resourceSourceMapper;
    private final ResourceMapper resourceMapper;

    public ResourceSourceServiceImpl(ResourceSourceMapper resourceSourceMapper, ResourceMapper resourceMapper) {
        this.resourceSourceMapper = resourceSourceMapper;
        this.resourceMapper = resourceMapper;
    }

    @Override
    public List<ResourceSourceVO> list(String authorizationStatus) {
        LambdaQueryWrapper<ResourceSource> wrapper = new LambdaQueryWrapper<ResourceSource>()
                .orderByDesc(ResourceSource::getUpdatedAt, ResourceSource::getId);
        if (StringUtils.hasText(authorizationStatus)) {
            wrapper.eq(ResourceSource::getAuthorizationStatus, authorizationStatus.trim());
        }
        return resourceSourceMapper.selectList(wrapper).stream().map(this::toVO).toList();
    }

    @Override
    public ResourceSourceVO getById(Long id) {
        return toVO(getOrThrow(id));
    }

    @Override
    public void create(ResourceSourceSaveRequest request) {
        ResourceSource source = new ResourceSource();
        BeanUtils.copyProperties(request, source);
        source.setAuthorizationStatus(AuthorizationStatus.PENDING.name());
        source.setRiskLevel(RiskLevel.MEDIUM.name());
        source.setAuditRemark(request.getAuditRemark() == null ? "" : request.getAuditRemark().trim());
        source.setAuditorName("");
        source.setAuditedAt(null);
        resourceSourceMapper.insert(source);
    }

    @Override
    public void update(Long id, ResourceSourceSaveRequest request) {
        ResourceSource source = getOrThrow(id);
        String currentAuthorizationStatus = source.getAuthorizationStatus();
        String currentRiskLevel = source.getRiskLevel();
        String currentAuditorName = source.getAuditorName();
        LocalDateTime currentAuditedAt = source.getAuditedAt();
        BeanUtils.copyProperties(request, source);
        source.setAuthorizationStatus(currentAuthorizationStatus);
        source.setRiskLevel(currentRiskLevel);
        source.setAuditorName(currentAuditorName);
        source.setAuditedAt(currentAuditedAt);
        resourceSourceMapper.updateById(source);
    }

    @Override
    public void audit(Long id, ResourceSourceAuditRequest request) {
        ResourceSource source = getOrThrow(id);
        source.setAuthorizationStatus(request.getAuthorizationStatus().trim());
        source.setAuditRemark(request.getAuditRemark() == null ? "" : request.getAuditRemark().trim());
        source.setRiskLevel(StringUtils.hasText(request.getRiskLevel()) ? request.getRiskLevel().trim() : source.getRiskLevel());
        source.setAuditorName(currentOperatorName());
        source.setAuditedAt(LocalDateTime.now());
        resourceSourceMapper.updateById(source);

        List<Resource> resources = resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getSourceId, id));
        for (Resource resource : resources) {
            resource.setAuthorizationStatusSnapshot(source.getAuthorizationStatus());
            if (!AuthorizationStatus.APPROVED.name().equals(source.getAuthorizationStatus())) {
                resource.setStatus(0);
                resource.setPublishTime(null);
            }
            resourceMapper.updateById(resource);
        }
    }

    @Override
    public long countPending() {
        Long count = resourceSourceMapper.selectCount(new LambdaQueryWrapper<ResourceSource>()
                .eq(ResourceSource::getAuthorizationStatus, AuthorizationStatus.PENDING.name()));
        return count == null ? 0L : count;
    }

    @Override
    public long countRisk() {
        Long count = resourceSourceMapper.selectCount(new LambdaQueryWrapper<ResourceSource>()
                .eq(ResourceSource::getAuthorizationStatus, AuthorizationStatus.RISK.name()));
        return count == null ? 0L : count;
    }

    private ResourceSource getOrThrow(Long id) {
        ResourceSource source = resourceSourceMapper.selectById(id);
        if (source == null) {
            throw new BusinessException("资料来源不存在");
        }
        return source;
    }

    private ResourceSourceVO toVO(ResourceSource source) {
        ResourceSourceVO vo = new ResourceSourceVO();
        BeanUtils.copyProperties(source, vo);
        return vo;
    }

    private String currentOperatorName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getName() != null) {
            return authentication.getName();
        }
        return "系统管理员";
    }
}
