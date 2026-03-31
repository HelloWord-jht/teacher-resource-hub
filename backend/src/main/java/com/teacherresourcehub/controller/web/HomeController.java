package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.common.api.Result;
import com.teacherresourcehub.service.HomeService;
import com.teacherresourcehub.vo.HomePageVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/web")
public class HomeController {

    private final HomeService homeService;

    public HomeController(HomeService homeService) {
        this.homeService = homeService;
    }

    @GetMapping("/home")
    public Result<HomePageVO> getHome() {
        return Result.success(homeService.getHomePage());
    }
}
