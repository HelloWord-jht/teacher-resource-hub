package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.FaqSaveRequest;
import com.teacherresourcehub.entity.Faq;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.FaqMapper;
import com.teacherresourcehub.service.FaqService;
import com.teacherresourcehub.vo.FaqVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FaqServiceImpl implements FaqService {

    private final FaqMapper faqMapper;

    public FaqServiceImpl(FaqMapper faqMapper) {
        this.faqMapper = faqMapper;
    }

    @Override
    public List<FaqVO> listEnabledFaqs() {
        return toFaqVOList(faqMapper.selectList(new LambdaQueryWrapper<Faq>()
                .eq(Faq::getStatus, 1)
                .orderByDesc(Faq::getSortOrder, Faq::getId)));
    }

    @Override
    public List<FaqVO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyList();
        }
        List<Faq> list = faqMapper.selectList(new LambdaQueryWrapper<Faq>()
                .in(Faq::getId, ids));
        Map<Long, FaqVO> map = toFaqVOList(list).stream()
                .collect(Collectors.toMap(FaqVO::getId, Function.identity(), (a, b) -> a));
        return ids.stream().map(map::get).filter(Objects::nonNull).toList();
    }

    @Override
    public List<FaqVO> listAdminFaqs() {
        return toFaqVOList(faqMapper.selectList(new LambdaQueryWrapper<Faq>()
                .orderByDesc(Faq::getSortOrder, Faq::getId)));
    }

    @Override
    public void createFaq(FaqSaveRequest request) {
        Faq faq = new Faq();
        BeanUtils.copyProperties(request, faq);
        faqMapper.insert(faq);
    }

    @Override
    public void updateFaq(Long id, FaqSaveRequest request) {
        Faq faq = getFaqOrThrow(id);
        BeanUtils.copyProperties(request, faq);
        faqMapper.updateById(faq);
    }

    @Override
    public void updateFaqStatus(Long id, Integer status) {
        Faq faq = getFaqOrThrow(id);
        faq.setStatus(status);
        faqMapper.updateById(faq);
    }

    @Override
    public void deleteFaq(Long id) {
        getFaqOrThrow(id);
        faqMapper.deleteById(id);
    }

    private Faq getFaqOrThrow(Long id) {
        Faq faq = faqMapper.selectById(id);
        if (faq == null) {
            throw new BusinessException("FAQ不存在");
        }
        return faq;
    }

    private List<FaqVO> toFaqVOList(List<Faq> list) {
        return list.stream().map(item -> {
            FaqVO vo = new FaqVO();
            BeanUtils.copyProperties(item, vo);
            return vo;
        }).toList();
    }
}
