package com.yupi.project.service.impl;

import com.yupi.project.service.UserInterfaceInfoService;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import javax.annotation.Resource;
@SpringBootTest
public class UserInterfaceInfoServiceImplTest {

    @Resource
    private UserInterfaceInfoService userInterfaceInfoService;

    @Test
    public void invokeCount() {
        boolean invokeCount = userInterfaceInfoService.invokeCount(1, 1);
        Assert.assertTrue(invokeCount);
    }
}