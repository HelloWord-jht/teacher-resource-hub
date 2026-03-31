package com.teacherresourcehub.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacherresourcehub.dto.LoginRequest;
import com.teacherresourcehub.entity.AdminUser;
import com.teacherresourcehub.exception.BusinessException;
import com.teacherresourcehub.mapper.AdminUserMapper;
import com.teacherresourcehub.security.JwtTokenService;
import com.teacherresourcehub.service.AuthService;
import com.teacherresourcehub.vo.LoginVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthServiceImpl implements AuthService {

    private final AdminUserMapper adminUserMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthServiceImpl(AdminUserMapper adminUserMapper,
                           PasswordEncoder passwordEncoder,
                           JwtTokenService jwtTokenService) {
        this.adminUserMapper = adminUserMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Override
    public LoginVO login(LoginRequest request) {
        AdminUser adminUser = adminUserMapper.selectOne(new LambdaQueryWrapper<AdminUser>()
                .eq(AdminUser::getUsername, request.getUsername())
                .eq(AdminUser::getStatus, 1)
                .last("LIMIT 1"));

        if (adminUser == null || !passwordEncoder.matches(request.getPassword(), adminUser.getPassword())) {
            throw new BusinessException(400, "用户名或密码错误");
        }

        adminUser.setLastLoginTime(LocalDateTime.now());
        adminUserMapper.updateById(adminUser);

        LoginVO vo = new LoginVO();
        vo.setUserId(adminUser.getId());
        vo.setUsername(adminUser.getUsername());
        vo.setNickname(adminUser.getNickname());
        vo.setToken(jwtTokenService.generateToken(adminUser.getId(), adminUser.getUsername()));
        return vo;
    }
}
