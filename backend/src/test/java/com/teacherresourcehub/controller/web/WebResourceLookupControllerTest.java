package com.teacherresourcehub.controller.web;

import com.teacherresourcehub.service.ResourceService;
import com.teacherresourcehub.vo.ResourceDetailVO;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WebResourceLookupControllerTest {

    @Test
    void shouldSupportLegacyResourceCodeLookupPath() throws Exception {
        ResourceService resourceService = mock(ResourceService.class);
        ResourceDetailVO detailVO = new ResourceDetailVO();
        detailVO.setId(2L);
        detailVO.setTitle("小学家长会 PPT + 发言稿通用包");
        detailVO.setResourceCode("RES-JZH-20260403-0001");

        when(resourceService.getWebResourceDetailByCode("RES-JZH-20260403-0001")).thenReturn(detailVO);

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(new WebResourceLookupController(resourceService))
                .build();

        mockMvc.perform(get("/api/web/resource-by-code/RES-JZH-20260403-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(0))
                .andExpect(jsonPath("$.data.resourceCode").value("RES-JZH-20260403-0001"))
                .andExpect(jsonPath("$.data.title").value("小学家长会 PPT + 发言稿通用包"));
    }
}
