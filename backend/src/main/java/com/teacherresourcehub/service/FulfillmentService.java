package com.teacherresourcehub.service;

import com.teacherresourcehub.common.api.PageResult;
import com.teacherresourcehub.dto.FulfillmentMarkDeliveredRequest;
import com.teacherresourcehub.vo.DeliveryRecordVO;
import com.teacherresourcehub.vo.FulfillmentQuickSearchItemVO;
import com.teacherresourcehub.vo.FulfillmentResourceVO;
import com.teacherresourcehub.vo.ResourceSearchLogVO;

import java.util.List;

public interface FulfillmentService {

    List<FulfillmentQuickSearchItemVO> quickSearch(String keyword);

    FulfillmentResourceVO getFulfillmentResource(String resourceCode);

    String buildDeliveryTemplate(String resourceCode);

    void markDelivered(FulfillmentMarkDeliveredRequest request);

    PageResult<DeliveryRecordVO> pageDeliveryRecords(String resourceCode, Long pageNum, Long pageSize);

    List<ResourceSearchLogVO> listRecentSearchLogs(int limit);
}
