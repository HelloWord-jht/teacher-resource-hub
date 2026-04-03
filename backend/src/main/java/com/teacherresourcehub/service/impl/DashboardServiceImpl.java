package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.common.enums.AuthorizationStatus;
import com.teacherresourcehub.entity.Category;
import com.teacherresourcehub.entity.DeliveryRecord;
import com.teacherresourcehub.entity.ImportTask;
import com.teacherresourcehub.entity.Lead;
import com.teacherresourcehub.entity.Resource;
import com.teacherresourcehub.mapper.CategoryMapper;
import com.teacherresourcehub.mapper.DeliveryRecordMapper;
import com.teacherresourcehub.mapper.ImportTaskMapper;
import com.teacherresourcehub.mapper.LeadMapper;
import com.teacherresourcehub.mapper.ResourceMapper;
import com.teacherresourcehub.service.CategoryService;
import com.teacherresourcehub.service.DashboardService;
import com.teacherresourcehub.service.LeadService;
import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.service.TagService;
import com.teacherresourcehub.vo.DashboardChannelStatVO;
import com.teacherresourcehub.vo.DashboardRecentResourceVO;
import com.teacherresourcehub.vo.DashboardResourceRankVO;
import com.teacherresourcehub.vo.DashboardVO;
import com.teacherresourcehub.vo.DeliveryRecordVO;
import com.teacherresourcehub.vo.ImportTaskVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final LeadMapper leadMapper;
    private final DeliveryRecordMapper deliveryRecordMapper;
    private final ImportTaskMapper importTaskMapper;

    public DashboardServiceImpl(ResourceService resourceService,
                                CategoryService categoryService,
                                TagService tagService,
                                LeadService leadService,
                                ResourceMapper resourceMapper,
                                CategoryMapper categoryMapper,
                                LeadMapper leadMapper,
                                DeliveryRecordMapper deliveryRecordMapper,
                                ImportTaskMapper importTaskMapper) {
        this.resourceService = resourceService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.leadService = leadService;
        this.resourceMapper = resourceMapper;
        this.categoryMapper = categoryMapper;
        this.leadMapper = leadMapper;
        this.deliveryRecordMapper = deliveryRecordMapper;
        this.importTaskMapper = importTaskMapper;
    }

    @Override
    public DashboardVO getDashboard() {
        LocalDateTime startOfToday = LocalDate.now().atStartOfDay();
        LocalDateTime endOfToday = startOfToday.plusDays(1);
        DashboardVO vo = new DashboardVO();
        vo.setResourceTotal(resourceService.countTotal());
        vo.setPublishedResourceTotal(countPublishedResources());
        vo.setPendingResourceTotal(countResourcesByAuthorizationStatus(AuthorizationStatus.PENDING.name()));
        vo.setRiskResourceTotal(countResourcesByAuthorizationStatus(AuthorizationStatus.RISK.name()));
        vo.setCategoryTotal(categoryService.countTotal());
        vo.setTagTotal(tagService.countTotal());
        vo.setLeadTotal(leadService.countTotal());
        vo.setTodayLeadTotal(countTodayLeads(startOfToday, endOfToday));
        vo.setTodayWechatAddedTotal(countTodayWechatAdded(startOfToday, endOfToday));
        vo.setTodayDealTotal(countTodayDeals(startOfToday, endOfToday));
        vo.setTodayDeliveryTotal(countTodayDeliveries(startOfToday, endOfToday));
        vo.setRecentResources(listRecentResources());
        vo.setRecentLeads(leadService.listRecentLeads(5));
        vo.setRecentDeliveries(listRecentDeliveries(5));
        vo.setRecentImportTasks(listRecentImportTasks(5));
        vo.setChannelLeadStats(listChannelLeadStats());
        vo.setHotViewResources(listHotResourcesByViews(5));
        vo.setHotConsultResources(listHotResourcesByConsults(5));
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

    private long countPublishedResources() {
        Long count = resourceMapper.selectCount(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getStatus, 1)
                .eq(Resource::getAuthorizationStatusSnapshot, AuthorizationStatus.APPROVED.name()));
        return count == null ? 0L : count;
    }

    private long countResourcesByAuthorizationStatus(String authorizationStatus) {
        Long count = resourceMapper.selectCount(new LambdaQueryWrapper<Resource>()
                .eq(Resource::getAuthorizationStatusSnapshot, authorizationStatus));
        return count == null ? 0L : count;
    }

    private long countTodayLeads(LocalDateTime startOfToday, LocalDateTime endOfToday) {
        Long count = leadMapper.selectCount(new LambdaQueryWrapper<Lead>()
                .ge(Lead::getCreatedAt, startOfToday)
                .lt(Lead::getCreatedAt, endOfToday));
        return count == null ? 0L : count;
    }

    private long countTodayWechatAdded(LocalDateTime startOfToday, LocalDateTime endOfToday) {
        Long count = leadMapper.selectCount(new LambdaQueryWrapper<Lead>()
                .eq(Lead::getWechatAddedStatus, 1)
                .ge(Lead::getLastFollowUpTime, startOfToday)
                .lt(Lead::getLastFollowUpTime, endOfToday));
        return count == null ? 0L : count;
    }

    private long countTodayDeals(LocalDateTime startOfToday, LocalDateTime endOfToday) {
        Long count = leadMapper.selectCount(new LambdaQueryWrapper<Lead>()
                .eq(Lead::getDealStatus, 1)
                .ge(Lead::getLastFollowUpTime, startOfToday)
                .lt(Lead::getLastFollowUpTime, endOfToday));
        return count == null ? 0L : count;
    }

    private long countTodayDeliveries(LocalDateTime startOfToday, LocalDateTime endOfToday) {
        Long count = deliveryRecordMapper.selectCount(new LambdaQueryWrapper<DeliveryRecord>()
                .ge(DeliveryRecord::getCreatedAt, startOfToday)
                .lt(DeliveryRecord::getCreatedAt, endOfToday));
        return count == null ? 0L : count;
    }

    private List<DeliveryRecordVO> listRecentDeliveries(int limit) {
        return deliveryRecordMapper.selectList(new LambdaQueryWrapper<DeliveryRecord>()
                        .orderByDesc(DeliveryRecord::getCreatedAt, DeliveryRecord::getId)
                        .last("LIMIT " + limit))
                .stream()
                .map(item -> {
                    DeliveryRecordVO vo = new DeliveryRecordVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                })
                .toList();
    }

    private List<ImportTaskVO> listRecentImportTasks(int limit) {
        return importTaskMapper.selectList(new LambdaQueryWrapper<ImportTask>()
                        .orderByDesc(ImportTask::getCreatedAt, ImportTask::getId)
                        .last("LIMIT " + limit))
                .stream()
                .map(item -> {
                    ImportTaskVO vo = new ImportTaskVO();
                    BeanUtils.copyProperties(item, vo);
                    return vo;
                })
                .toList();
    }

    private List<DashboardChannelStatVO> listChannelLeadStats() {
        return leadMapper.selectList(new LambdaQueryWrapper<Lead>())
                .stream()
                .collect(Collectors.groupingBy(lead -> lead.getChannel() == null || lead.getChannel().isBlank() ? "unknown" : lead.getChannel(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    DashboardChannelStatVO vo = new DashboardChannelStatVO();
                    vo.setChannel(entry.getKey());
                    vo.setLeadCount(entry.getValue());
                    return vo;
                })
                .sorted((a, b) -> Long.compare(b.getLeadCount(), a.getLeadCount()))
                .toList();
    }

    private List<DashboardResourceRankVO> listHotResourcesByViews(int limit) {
        return resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getAuthorizationStatusSnapshot, AuthorizationStatus.APPROVED.name())
                        .eq(Resource::getStatus, 1)
                        .orderByDesc(Resource::getViewCount, Resource::getId)
                        .last("LIMIT " + limit))
                .stream()
                .map(item -> toRankVO(item, item.getViewCount()))
                .toList();
    }

    private List<DashboardResourceRankVO> listHotResourcesByConsults(int limit) {
        return resourceMapper.selectList(new LambdaQueryWrapper<Resource>()
                        .eq(Resource::getAuthorizationStatusSnapshot, AuthorizationStatus.APPROVED.name())
                        .eq(Resource::getStatus, 1)
                        .orderByDesc(Resource::getConsultCount, Resource::getId)
                        .last("LIMIT " + limit))
                .stream()
                .map(item -> toRankVO(item, item.getConsultCount()))
                .toList();
    }

    private DashboardResourceRankVO toRankVO(Resource resource, Integer countValue) {
        DashboardResourceRankVO vo = new DashboardResourceRankVO();
        vo.setResourceId(resource.getId());
        vo.setResourceCode(resource.getResourceCode());
        vo.setTitle(resource.getTitle());
        vo.setCountValue(countValue == null ? 0L : countValue.longValue());
        return vo;
    }
}
