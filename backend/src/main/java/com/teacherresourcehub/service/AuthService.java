package com.teacherresourcehub.service;

import com.teacherresourcehub.dto.LoginRequest;
import com.teacherresourcehub.vo.LoginVO;

public interface AuthService {

    LoginVO login(LoginRequest request);
}
