package com.teacherresourcehub.controller.admin;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.dto.LeadChannelSaveRequest;
import com.teacherresourcehub.service.LeadChannelService;
import com.teacherresourcehub.vo.LeadChannelVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/lead-channels")
public class AdminLeadChannelController {

    private final LeadChannelService leadChannelService;

    public AdminLeadChannelController(LeadChannelService leadChannelService) {
        this.leadChannelService = leadChannelService;
    }

    @GetMapping
    public Result<List<LeadChannelVO>> list() {
        return Result.success(leadChannelService.list());
    }

    @GetMapping("/{id}")
    public Result<LeadChannelVO> detail(@PathVariable Long id) {
        return Result.success(leadChannelService.getById(id));
    }

    @PostMapping
    public Result<Void> create(@Valid @RequestBody LeadChannelSaveRequest request) {
        leadChannelService.create(request);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @Valid @RequestBody LeadChannelSaveRequest request) {
        leadChannelService.update(id, request);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        leadChannelService.delete(id);
        return Result.success();
    }
}
