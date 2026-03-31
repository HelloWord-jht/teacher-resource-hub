package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.FaqSaveRequest;
import com.teacherresourcehub.vo.FaqVO;

import java.util.List;

public interface FaqService {

    List<FaqVO> listEnabledFaqs();

    List<FaqVO> listByIds(List<Long> ids);

    List<FaqVO> listAdminFaqs();

    void createFaq(FaqSaveRequest request);

    void updateFaq(Long id, FaqSaveRequest request);

    void updateFaqStatus(Long id, Integer status);

    void deleteFaq(Long id);
}
